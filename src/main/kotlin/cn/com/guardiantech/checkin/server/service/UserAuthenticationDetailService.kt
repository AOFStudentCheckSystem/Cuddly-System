package cn.com.guardiantech.checkin.server.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
@Service
class UserAuthenticationDetailService: UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        println(username)
        return null
    }
}