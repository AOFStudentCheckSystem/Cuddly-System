package cn.com.guardiantech.checkin.server.service

import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.repository.UserTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.NoSuchElementException

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@Service
class AuthenticationService {
    @Autowired
    lateinit var userTokenRepository: UserTokenRepository

    fun validateToken(secret: String): UserToken {
        userTokenRepository.removeExpiredTokens()
        try {
            return userTokenRepository.findByTokenSecretIgnoreCase(secret).get()
        } catch (e: NoSuchElementException) {
            throw UnauthorizedException()
        }
    }
}