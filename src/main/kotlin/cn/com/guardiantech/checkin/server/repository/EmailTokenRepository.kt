package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.authentication.EmailToken
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
interface EmailTokenRepository : CrudRepository<EmailToken, String>{
    @Cacheable("email_tokens")
    fun findByTokenSecretIgnoreCase(secret: String): Optional<UserToken>

    @Query("delete from email_tokens where creation_date < DATE_SUB(NOW(), INTERVAL ?1 DAY)", nativeQuery = true)
    @Modifying
    @Transactional
    fun removeExpiredTokens(secondsToExpire: Long = 14)
}