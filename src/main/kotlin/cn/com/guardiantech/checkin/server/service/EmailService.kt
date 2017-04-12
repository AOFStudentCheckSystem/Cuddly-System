package cn.com.guardiantech.checkin.server.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.Address
import javax.mail.Message
import javax.mail.internet.InternetAddress

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
@Service
class EmailService {
    @Autowired
    lateinit var mailSender: JavaMailSender

    fun sendMail(templateName: String, values: HashMap<String, String>, vararg recipients: String) {
        val msg = mailSender.createMimeMessage()
        msg.subject = "AOF Check In Test Mail"
        msg.setText("This is sample text")
        recipients.mapTo(HashSet<Address>(), ::InternetAddress).forEach {
            msg.addRecipient(Message.RecipientType.TO, it)
        }
        mailSender.send(msg)
    }
}