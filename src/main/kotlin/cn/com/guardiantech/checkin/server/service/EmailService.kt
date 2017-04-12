package cn.com.guardiantech.checkin.server.service

import cn.com.guardiantech.checkin.server.mail.MailTemplateFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.Address
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.internet.InternetAddress

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
@Service
class EmailService {
    @Value("\${spring.mail.from}")
    lateinit var from: String

    @Autowired
    lateinit var mailSender: JavaMailSender

    fun sendMail(templateName: String, values: HashMap<String, String>, vararg recipients: String) {
        val template = MailTemplateFactory.createTemlateByFileName(if (templateName.endsWith(".template",true)) templateName else templateName + ".template")
        values.forEach {
            template.setStringValue(it.key, it.value)
        }
        val msg = mailSender.createMimeMessage()
        msg.setFrom(from)
        msg.subject = "AOF Check In Test Mail"
        msg.setText(template.encode(), "utf-8", "html")
        recipients.mapTo(HashSet<Address>(), ::InternetAddress).forEach {
            msg.addRecipient(Message.RecipientType.TO, it)
        }
        mailSender.send(msg)
    }
}