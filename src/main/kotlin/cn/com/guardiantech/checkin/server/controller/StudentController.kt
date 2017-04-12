package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import cn.com.guardiantech.checkin.server.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
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
    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var userRepositopry: UserRepository

    @RequestMapping(path = arrayOf("/create", "/new"), method = arrayOf(RequestMethod.POST))
    fun createStudent(@RequestParam("firstName") firstname: String,
                      @RequestParam("lastName") lastName: String,
                      @RequestParam("preferredName", required = false, defaultValue = "") preferredName: String,
                      @RequestParam("idNumber") studentID: String): ResponseEntity<String> {
        val pfn = if (preferredName == "") firstname else preferredName
        val student = Student(studentID, lastName, firstname, preferredName)
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
}