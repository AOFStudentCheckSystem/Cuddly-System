package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EmailTokenRepository
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.repository.UserTokenRepository
import cn.com.guardiantech.checkin.server.service.UserRegistrationService
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/auth"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class AuthController {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userTokenRepository: UserTokenRepository

    @Autowired
    lateinit var emailTokenRepository: EmailTokenRepository

    @Autowired
    lateinit var registerServce: UserRegistrationService

//    @RequestMapping(path = arrayOf("/verify-token"), method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
//    fun verifyEmailAddress(@RequestParam("token", required = false, defaultValue = "") token: String): String {
//        if (token.isNotEmpty()) {
//            //Verify token here
//            response.headers.add("Location", "https://www.aofactivities.com")
////            response. = HttpStatus.MOVED_PERMANENTLY
//        }
//        return response
//    }

    @RequestMapping(path = arrayOf("/logout"))
    fun revokeToken(@AuthenticationPrincipal p: Token) {
        userTokenRepository.deleteByTokenSecretIgnoreCase(p.tokenSecret)
    }

    @RequestMapping(path = arrayOf("/register"), method = arrayOf(RequestMethod.POST))
    fun registerUser(@RequestParam("email") email: String,
                     @RequestParam("password") password: String): ResponseEntity<String> = ActionResult(registerServce.registerUserWithEmailAndPassword(email, password)).encode()

    @RequestMapping(path = arrayOf("/auth", "/login"), method = arrayOf(RequestMethod.POST))
    fun authenticate(@RequestParam("email") email: String,
                     @RequestParam("password") password: String): UserToken {
        val response = JSONObject()
        try {
            val user = userRepository.findByEmailIgnoreCase(email).get()
            val passwd = DigestUtils.sha256Hex(password)
            if (passwd == user.passwordHash) {
                return userTokenRepository.save(UserToken(user))
            } else {
                throw UnauthorizedException()
            }
        } catch (e: Throwable) {
            throw UnauthorizedException()
        }
    }

    @RequestMapping(path = arrayOf("/verify"))
    fun verifyToken(@AuthenticationPrincipal token: Token) = token

    @CacheEvict(cacheNames = arrayOf("user_tokens"), allEntries = true)
    @RequestMapping(path = arrayOf("/admin/setLevel"), method = arrayOf(RequestMethod.POST))
    fun setUserLevel(@RequestParam("targetUser") targetUser: String,
                     @RequestParam("targetLevel") level: Int) {
        val user = userRepository.findByEmailIgnoreCase(targetUser).get()
        user.userLevel = level
        userRepository.save(user)
    }
}