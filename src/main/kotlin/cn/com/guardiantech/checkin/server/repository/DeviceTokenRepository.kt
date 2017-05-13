package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.entity.pushNotification.PushNotificationDevice
import org.springframework.data.repository.CrudRepository

/**
 * Created by Codetector on 2017/5/2.
 * Project backend
 */
interface DeviceTokenRepository: CrudRepository<PushNotificationDevice, String>{
    fun findByUser(user: User): List<PushNotificationDevice>
}