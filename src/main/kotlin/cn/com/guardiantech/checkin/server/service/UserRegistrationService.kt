package cn.com.guardiantech.checkin.server.service

import cn.com.guardiantech.checkin.server.entity.authentication.EmailVerificationToken
import cn.com.guardiantech.checkin.server.entity.authentication.User
import cn.com.guardiantech.checkin.server.mail.MailTemplateFactory
import cn.com.guardiantech.checkin.server.repository.EmailVerificationTokenRepository
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.utils.isValidEmailAddress
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
@Service
class UserRegistrationService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var verifyTokenRepository: EmailVerificationTokenRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var mailService: EmailService

    fun registerUserWithEmailAndPassword(email: String, passwordHash: String) : Boolean{
        if (!isValidEmailAddress(email))
            throw IllegalArgumentException("Invalid Email Address")
        val newUser = userRepository.findByEmailIgnoreCase(email)
        if (newUser.isPresent) {
            return false
        }
        val oldToken = verifyTokenRepository.findByEmailIgnoreCase(email.toLowerCase())
        if (oldToken.isPresent)
            verifyTokenRepository.delete(oldToken.get())

        //Create Token
        var token = EmailVerificationToken(email, DigestUtils.sha256Hex(passwordHash))
        val student = studentRepository.findByEmailIgnoreCase(email)
        if (student.isPresent) {
            token.linkedStudent = student.get()
        }
        token = verifyTokenRepository.save(token)

        val mail = MailTemplateFactory.createTemplateByFileName("emailVerification")
        if (student.isPresent) {
            val stu = student.get()
            mail.setStringValue("firstName", stu.firstName.capitalize())
            mail.setStringValue("lastName", stu.lastName.capitalize())
        }
        mail.setStringValue("link", token.id!!)
        mailService.sendMail(mail, email)
        return true
    }
}