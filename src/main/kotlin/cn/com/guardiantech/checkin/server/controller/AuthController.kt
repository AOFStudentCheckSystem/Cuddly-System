package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.UserRepository
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/auth"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class AuthController {

    @Autowired
    lateinit var userRepository: UserRepository

    @RequestMapping(path = arrayOf("/register"), method = arrayOf(RequestMethod.POST))
    fun registerUser(@RequestParam(name = "email") email: String,
                     @RequestParam(name = "password") password: String): ResponseEntity<String> {
        try {
            userRepository.save(User(email, DigestUtils.sha256Hex(password)))
            return ActionResult(true).encode()
        } catch (e: Throwable) {
            return ActionResult(e).encode()
        }
    }
}