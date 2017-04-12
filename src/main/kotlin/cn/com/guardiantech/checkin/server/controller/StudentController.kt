package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/**
 * Created by Codetector on 2017/4/11.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/student"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE) )
class StudentController {
    @Value("\${gcheck.defaultEmailDomain}")
    lateinit var defualtEmailDomain: String

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var userRepositopry: UserRepository

    @Autowired
    lateinit var emailService: EmailService

    @RequestMapping(path = arrayOf("/create", "/new"), method = arrayOf(RequestMethod.POST))
    fun createStudent(@RequestParam("firstName") firstname: String,
                      @RequestParam("lastName") lastName: String,
                      @RequestParam("preferredName", required = false, defaultValue = "") preferredName: String,
                      @RequestParam("idNumber") studentID: String,
                      @RequestParam("email", required = false, defaultValue = "") email: String): ResponseEntity<String> {
        val pfn = if (preferredName.isEmpty()) firstname else preferredName
        val eml: String = if (email.isEmpty()) {
            lastName.toLowerCase() + if (firstname.isEmpty()) "" else firstname[0] + defualtEmailDomain
        } else {email.toLowerCase()}
        val student = Student(studentID, lastName, firstname, pfn, eml)
        studentRepository.save(student)
        return ActionResult(success = true).encode()
    }

    @RequestMapping(path = arrayOf("/templink/{studentId}"))
    fun tempLink(@AuthenticationPrincipal token: Token,
                 @PathVariable("studentId") studentID: String): ResponseEntity<String> {
        if (token is UserToken) {
            token.user.student = studentRepository.findByIdNumberIgnoreCase(idNumber = studentID).get()
            userRepositopry.save(token.user)
        } else {
            return ActionResult(false, HttpStatus.NOT_ACCEPTABLE).encode()
        }
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/templink/sendmail"))
    fun tempEmail(@RequestParam("destAddr") addr: String): ResponseEntity<String> {
        emailService.sendMail("", hashMapOf(), addr)
        return ActionResult(true).encode()
    }
}