package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.authentication.EmailVerificationToken
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
interface EmailVerificationTokenRepository : CrudRepository<EmailVerificationToken, String> {

    fun findById(id: String): Optional<EmailVerificationToken>

}