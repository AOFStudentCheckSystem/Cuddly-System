package cn.com.guardiantech.checkin.server.authentication

import org.springframework.security.core.GrantedAuthority

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
enum class Permission(val permissionLevel: Int, val stringValue: String): GrantedAuthority {
    SIGNUP(0, "SignUp"),
    USER(1, "User"),
    TABLET(900, "Tablet"),
    ADMIN(1000, "Admin");

    override fun getAuthority(): String = stringValue
}