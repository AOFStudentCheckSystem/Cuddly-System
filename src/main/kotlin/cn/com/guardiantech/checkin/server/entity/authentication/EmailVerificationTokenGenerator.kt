package cn.com.guardiantech.checkin.server.entity.authentication

import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable
import java.util.*

/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
class EmailVerificationTokenGenerator : IdentifierGenerator{
    override fun generate(session: SessionImplementor, obj: Any?): Serializable? {
        val connection = session.connection()
        var validToken:Boolean
        var token: String
        do {
            token = UUID.randomUUID().toString().replace("-","").toUpperCase()
            val stmt = connection.prepareStatement("SELECT count(*) as cnt FROM `email_verification_token` WHERE email_verify_token = ?")
            stmt.setString(1, token)
            val rs = stmt.executeQuery()
            rs.next()
            validToken = (rs.getInt(1) < 1)
        } while (!validToken)
        return token
    }
}