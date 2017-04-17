package cn.com.guardiantech.checkin.server.entity.authentication

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
@Entity
class EmailVerificationToken {
    @Id
    @GenericGenerator(name = "uuidGenerator", strategy = "cn.com.guardiantech.checkin.server.entity.authentication.EmailVerificationTokenGenerator")
    @GeneratedValue(generator = "uuidGenerator")
    @Column(name = "email_verify_token")
    var id: String? = null



}