package cn.com.guardiantech.checkin.server.authentication

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
interface Token {
    var tokenSecret: String
    fun isAuthenticated(permission: Permission): Boolean
}