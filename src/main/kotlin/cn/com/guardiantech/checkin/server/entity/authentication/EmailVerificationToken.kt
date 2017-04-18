package cn.com.guardiantech.checkin.server.entity.authentication

import cn.com.guardiantech.checkin.server.entity.Student
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
@Entity
class EmailVerificationToken() {
    @Id
    @GenericGenerator(name = "uuidGenerator", strategy = "cn.com.guardiantech.checkin.server.entity.authentication.EmailVerificationTokenGenerator")
    @GeneratedValue(generator = "uuidGenerator")
    @Column(name = "email_verify_token")
    var id: String? = null

    @Column(unique = true, length = 256)
    var email: String = ""

    var passwordHash: String = ""

    @ManyToOne(optional = true)
    var linkedStudent: Student? = null

    constructor(email: String, passwordHash: String) : this() {
        this.email = email
        this.passwordHash = passwordHash
    }

    constructor(email: String, passwordHash: String, linkedStudent: Student?) : this() {
        this.email = email
        this.passwordHash = passwordHash
        this.linkedStudent = linkedStudent
    }


}