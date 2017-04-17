package cn.com.guardiantech.checkin.server.service

import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.repository.EmailTokenRepository
import cn.com.guardiantech.checkin.server.repository.UserTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@Service
class AuthenticationService {
    @Autowired
    lateinit var userTokenRepository: UserTokenRepository

    @Autowired
    lateinit var emailTokenRepository: EmailTokenRepository

    fun validateToken(secret: String): Token {
        userTokenRepository.removeExpiredTokens()
        emailTokenRepository.removeExpiredTokens()
        try {
            val userTokenFind = userTokenRepository.findByTokenSecretIgnoreCase(secret)
            val token: Token
            if (userTokenFind.isPresent) {
                token = userTokenFind.get()
                if (!token.user.enabled) {
                    throw UnauthorizedException()
                }
                token.lastActive = Date()
                userTokenRepository.save(token)
            } else {
                token = emailTokenRepository.findByTokenSecretIgnoreCase(secret).get()
            }
            return token
        } catch (e: NoSuchElementException) {
            throw UnauthorizedException()
        }
    }
}