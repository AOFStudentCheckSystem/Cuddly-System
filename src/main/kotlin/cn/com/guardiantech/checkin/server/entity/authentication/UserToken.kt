package cn.com.guardiantech.checkin.server.entity.authentication

import cn.com.guardiantech.checkin.server.authentication.Permission
import cn.com.guardiantech.checkin.server.authentication.Token
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
@Entity
@Table(name = "user_tokens")
class UserToken() : Token {
    constructor(user: User) : this() {
        this.user = user
    }

    @Id
    override var tokenSecret: String = UUID.randomUUID().toString()

    @OneToOne(optional = false, orphanRemoval = true)
    lateinit var user: User

    var lastActive: Date = Date()

    override fun isAuthenticated(permission: Permission): Boolean {
        return user.userLevel >= permission.permissionLevel
    }
}