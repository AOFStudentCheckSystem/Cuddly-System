package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import cn.com.guardiantech.checkin.server.repository.UserRepository
import cn.com.guardiantech.checkin.server.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
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
@RequestMapping(path = arrayOf("/student"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
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
        } else {
            email.toLowerCase()
        }
        val student = Student(studentID, lastName, firstname, pfn, eml)
        studentRepository.save(student)
        return ActionResult(success = true).encode()
    }

    @RequestMapping(path = arrayOf("/listall"), method = arrayOf(RequestMethod.GET))
    fun listAllStudents() = studentRepository.findAll()

    @RequestMapping(path = arrayOf("/list"), method = arrayOf(RequestMethod.GET))
    fun listAllStudentPaged(pageable: Pageable) = studentRepository.findAll(pageable)

    @RequestMapping(path = arrayOf("/list/{studentId}"), method = arrayOf(RequestMethod.GET))
    fun listOneStudentById(@PathVariable("studentId") studentID: String): Student {
        return studentRepository.findByIdNumberIgnoreCase(studentID).get()
    }

    @RequestMapping(path = arrayOf("/edit"), method = arrayOf(RequestMethod.POST))
    fun updateStudent(@RequestParam("idNumber") studentId: String,
                      @RequestParam("firstName", required = true, defaultValue = "") firstName: String,
                      @RequestParam("lastName", required = true, defaultValue = "") lastName: String,
                      @RequestParam("preferredName", required = true, defaultValue = "") pName: String,
                      @RequestParam("email", required = true, defaultValue = "") email: String,
                      @RequestParam("cardSecret", required = true, defaultValue = "") rfid: String): ResponseEntity<String> {
        val targetStudent = studentRepository.findByIdNumberIgnoreCase(studentId).get()
        if (firstName.isNotEmpty()) {
            targetStudent.firstName = firstName
        }
        if (lastName.isNotEmpty()) {
            targetStudent.lastName = lastName
        }
        if (pName.isNotEmpty()) {
            targetStudent.preferredName = pName
        }
        if (email.isNotEmpty()) {
            targetStudent.email = email
        }
        if (rfid.isNotEmpty()) {
            targetStudent.cardSecret = rfid.toUpperCase()
        }
        studentRepository.save(targetStudent)
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/clear-card"), method = arrayOf(RequestMethod.POST))
    fun clearCardRFID(@RequestParam("studentId") studentId: String): ResponseEntity<String> {
        val targetStudent = studentRepository.findByIdNumberIgnoreCase(idNumber = studentId).get()
        targetStudent.cardSecret = null
        studentRepository.save(targetStudent)
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/bind-card"), method = arrayOf(RequestMethod.POST))
    fun bindCardWithStudent(@RequestParam("cardSecret") card: String,
                            @RequestParam("studentId") student: String): ResponseEntity<String> {
        val returnJson = JSONObject()
        val targetStudent = studentRepository.findByIdNumberIgnoreCase(student).get()

        val oldOwner_O = studentRepository.findByCardSecretIgnoreCase(card)
        returnJson.put("override", oldOwner_O.isPresent)
        if (oldOwner_O.isPresent) {
            val os = oldOwner_O.get()
            os.cardSecret = null
            studentRepository.save(os)
            returnJson.put("oldOwner", os.idNumber)
        }
        targetStudent.cardSecret = card
        studentRepository.save(targetStudent)
        return ActionResult(true).encode()
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
        emailService.sendMail("tokenNotification", hashMapOf(Pair("link", "http://www.google.com")), addr)
        return ActionResult(true).encode()
    }
}