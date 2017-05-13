package cn.com.guardiantech.checkin.server.entity.pushNotification

import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.User
import javax.persistence.*

/**
 * Created by Codetector on 2017/5/2.
 * Project backend
 */
@Entity
@Table(name = "devices")
class PushNotificationDevice {
    @Id
    var deviceId: String = ""

    @Enumerated(EnumType.STRING)
    var tokenType: DeviceTokenType = DeviceTokenType.Android

    @ManyToOne
    var assosicatedAccount: User? = null

    @ManyToOne
    var assosicatedStudent: Student? = null
}