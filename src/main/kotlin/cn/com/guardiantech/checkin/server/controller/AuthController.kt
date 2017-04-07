package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.repository.UserTokenRepository
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
    fun revokeToken(@RequestParam("token") token: String) {
        userTokenRepository.deleteByTokenSecretIgnoreCase(token)
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

    @RequestMapping(path = arrayOf("/auth"), method = arrayOf(RequestMethod.POST))
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
        } catch (e: NoSuchElementException) {
            throw UnauthorizedException()
        }
    }
}