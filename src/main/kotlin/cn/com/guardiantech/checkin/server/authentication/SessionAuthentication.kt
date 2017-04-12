package cn.com.guardiantech.checkin.server.authentication

import org.hibernate.mapping.Collection
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority


/**
 * Created by Codetector on 2017/3/29.
 * All rights reserved.
 */
class SessionAuthentication(private val token: Token) : Authentication {
    private var authenticated = true

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Permission.values().filter {
            token.isAuthenticated(it)
        }.mapTo(HashSet<GrantedAuthority>()) {
            it
        }
    }

    override fun setAuthenticated(p0: Boolean) {
        authenticated = p0
    }

    override fun getName(): String = ""

    override fun getCredentials(): Any = token.tokenSecret

    override fun getPrincipal(): Any = token

    override fun isAuthenticated(): Boolean = authenticated

    override fun getDetails(): Any = token
}