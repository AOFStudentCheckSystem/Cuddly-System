package cn.com.guardiantech.checkin.server.entity.authentication

import cn.com.guardiantech.checkin.server.authentication.Permission
import cn.com.guardiantech.checkin.server.authentication.Token
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
@Table(name = "user_tokens")
class UserToken() : Token {
    constructor(user: User) : this() {
        this.user = user
        this.tokenSecret = UUID.randomUUID().toString()
    }

    @Id
    @Column(name = "token")
    @JsonProperty("token")
    override var tokenSecret: String = ""

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var user: User

    @JsonIgnore
    var lastActive: Date = Date()

    override fun isAuthenticated(permission: Permission): Boolean {
        return user.userLevel >= permission.permissionLevel
    }
}