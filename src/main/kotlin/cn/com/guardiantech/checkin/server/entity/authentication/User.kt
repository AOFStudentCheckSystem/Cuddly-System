package cn.com.guardiantech.checkin.server.entity.authentication

import cn.com.guardiantech.checkin.server.entity.Student
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@Entity
class User {
    constructor(email: String, passwordHash: String) {
        this.email = email
        this.passwordHash = passwordHash
    }

    constructor(email: String, passwordHash: String, student: Student?) {
        this.email = email
        this.passwordHash = passwordHash
        this.student = student
    }

    @Id
    @GeneratedValue
    var userId: Long = 0

    @Column(unique = true)
    var email: String = ""

    var userLevel: Int = 1

    @JsonIgnore
    lateinit var passwordHash: String

    @OneToOne(optional = true, orphanRemoval = true)
    var student: Student? = null
}