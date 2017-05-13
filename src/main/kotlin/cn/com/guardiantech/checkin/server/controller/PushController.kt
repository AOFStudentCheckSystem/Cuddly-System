package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.entity.pushNotification.DeviceTokenType
import cn.com.guardiantech.checkin.server.exception.PermissionDeniedException
import cn.com.guardiantech.checkin.server.service.notification.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Codetector on 2017/5/2.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/push"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class PushController(@Autowired var notificationService: NotificationService) {
    @RequestMapping(path = arrayOf("/bind"))
    fun bindAccountToDeviceID(@AuthenticationPrincipal token: Token,
                              @RequestParam("deviceId") deviceId: String,
                              @RequestParam("deviceType", defaultValue = "android") deviceType: String) {
        val tokenType: DeviceTokenType
        when(deviceType.toLowerCase()) {
            "android" -> {
                tokenType = DeviceTokenType.Android
            }
            "ios" -> {
                tokenType = DeviceTokenType.iOS
            }
            else -> {
                throw IllegalArgumentException("Malformed deviceType, it can only be either 'ios' or 'android'")
            }
        }
        if (token !is UserToken) {
            throw PermissionDeniedException()
        }
        val user:User = token.user


    }
}