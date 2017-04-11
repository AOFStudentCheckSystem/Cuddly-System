package cn.com.guardiantech.checkin.server.entity.authentication

import cn.com.guardiantech.checkin.server.authentication.Permission
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.Student
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@Entity
@Table(name = "email_tokens")
class EmailToken(): Token{
    constructor(student: Student) : this() {
        this.student = student
        this.tokenSecret = UUID.randomUUID().toString()
    }

    @Id
    @Column(name = "token")
    @JsonProperty("token")
    override var tokenSecret: String = ""

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var student: Student

    @JsonIgnore
    var creationDate: Date = Date()

    override fun isAuthenticated(permission: Permission): Boolean {
        return Permission.SIGNUP.permissionLevel >= permission.permissionLevel
    }

    override fun student(): Student? {
        return student
    }

}