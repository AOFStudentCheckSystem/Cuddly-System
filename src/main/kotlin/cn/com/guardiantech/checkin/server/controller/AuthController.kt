package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.repository.UserTokenRepository
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

    @RequestMapping(path = arrayOf("/logout"))
    fun revokeToken(@AuthenticationPrincipal p: Token) {
        userTokenRepository.deleteByTokenSecretIgnoreCase(p.tokenSecret)
    }

    @RequestMapping(path = arrayOf("/register"), method = arrayOf(RequestMethod.POST))
    fun registerUser(@RequestParam("email") email: String,
                     @RequestParam("password") password: String): ResponseEntity<String> {
        try {
            userRepository.save(User(email, DigestUtils.sha256Hex(password)))
            return ActionResult(true).encode()
        } catch (e: Throwable) {
            return ActionResult(e).encode()
        }
    }

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