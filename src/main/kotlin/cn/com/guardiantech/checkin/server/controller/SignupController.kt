package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONArray
import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.*
import cn.com.guardiantech.checkin.server.entity.authentication.EmailToken
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.mail.MailTemplate
import cn.com.guardiantech.checkin.server.mail.MailTemplateFactory
import cn.com.guardiantech.checkin.server.repository.*
import cn.com.guardiantech.checkin.server.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Codetector on 2017/4/10.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/signup"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class SignupController {
    @Autowired lateinit var sheetRepository: SignUpSheetRepository
    @Autowired lateinit var signupSheetEntryRepository: SignupSheetEntryRepository
    @Autowired lateinit var eventRepository: EventRepository
    @Autowired lateinit var eventRecordRepository: EventRecordRepository
    @Autowired lateinit var groupRepository: EventGroupRepository
    @Autowired lateinit var studentRepository: StudentRepository
    @Autowired lateinit var emailTokenRepository: EmailTokenRepository
    @Autowired lateinit var emailService: EmailService

    @RequestMapping(path = arrayOf("/edit/{id}"), method = arrayOf(RequestMethod.DELETE))
    fun removeSheet(@PathVariable("id") id: Long): ResponseEntity<String> {
        return ActionResult(sheetRepository.removeById(id) > 0).encode()
    }

    @RequestMapping(path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    fun createSheet(@RequestParam("name") sheetName: String,
                    @RequestParam("groups", required = false, defaultValue = "[]") groups: String): ResponseEntity<String> {
        try {
            val sheet = sheetRepository.save(SignUpSheet(sheetName))
            val jsonArrayGroup = JSONArray(groups)
            val eventGroupList: MutableList<SignupSheetEntry> = arrayListOf()
            jsonArrayGroup.forEach {
                val id = (it as JSONObject).getLong("eventGroupId")
                val weight = it.getInteger("weight")
                val targetGroup = groupRepository.findById(id).get()
                var entry = SignupSheetEntry(targetGroup, sheet, weight)
                entry = signupSheetEntryRepository.save(entry)
                eventGroupList.add(entry)
            }
            sheet.entries.addAll(eventGroupList)
            sheetRepository.save(sheet)
            return ActionResult(true).encode()
        } catch (e: Throwable) {
            return ActionResult(e).encode()
        }
    }

    @RequestMapping(path = arrayOf("/edit/{id}/add"), method = arrayOf(RequestMethod.POST))
    fun addGroupToSheet(@RequestParam("group") group: Long,
                        @RequestParam("weight") weight: Int,
                        @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        sht.entries.add(signupSheetEntryRepository.save(SignupSheetEntry(grp, sht, weight)))
        sheetRepository.save(sht)
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{id}/remove"), method = arrayOf(RequestMethod.POST))
    fun removeGroupFromSheet(@RequestParam("group") group: Long,
                             @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        val result = sht.entries.remove(signupSheetEntryRepository.findByEventGroupAndSheet(grp, sht).get())
        sheetRepository.save(sht)
        return ActionResult(result, HttpStatus.NOT_ACCEPTABLE).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{id}/set"), method = arrayOf(RequestMethod.POST))
    fun setGroupToSheet(@RequestParam("group") groups: String,
                        @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        try {
            var sheet = sheetRepository.findById(sheetId).get()
            val jsonArrayGroup = JSONArray(groups)
            val eventGroupList: MutableList<SignupSheetEntry> = arrayListOf()
            sheet.entries.clear()
            sheet = sheetRepository.save(sheet)
            jsonArrayGroup.forEach {
                val id = (it as JSONObject).getLong("eventGroupId")
                val weight = it.getInteger("weight")
                val targetGroup = groupRepository.findById(id).get()
                var entry = SignupSheetEntry(targetGroup, sheet, weight)
                entry = signupSheetEntryRepository.save(entry)
                eventGroupList.add(entry)
            }
            sheet.entries.addAll(eventGroupList)
            sheetRepository.save(sheet)
            return ActionResult(true).encode()
        } catch (e: Throwable) {
            return ActionResult(e).encode()
        }
    }

    @RequestMapping(path = arrayOf("/edit/{id}"), method = arrayOf(RequestMethod.POST))
    fun editSignupSheet(@PathVariable("id") id:Long,
                        @RequestParam("newName", required = false, defaultValue = "") name: String,
                        @RequestParam("newStatus", required = false, defaultValue = "") status: String): SignUpSheet? {
        val targetSheet = sheetRepository.findById(id).get()
        if (name.isNotEmpty()) {
            targetSheet.name = name
        }
        if (status.isNotBlank()) {
            val statusI = status.toInt()
            if (statusI in 0..2) {
                targetSheet.status = statusI
            }
        }
        return sheetRepository.save(targetSheet)
    }

    @RequestMapping(path = arrayOf("/find/{id}"), method = arrayOf(RequestMethod.GET))
    fun findById(@PathVariable id: Long): SignUpSheet {
        return sheetRepository.findById(id).get()
    }

    @RequestMapping(path = arrayOf("/list"), method = arrayOf(RequestMethod.GET))
    fun listAll(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", sheetRepository.findAll()), HttpStatus.OK)
    }

    @RequestMapping(path = arrayOf("/list/current"), method = arrayOf(RequestMethod.GET))
    fun listOpenSheet(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(1)), HttpStatus.OK)
    }

    @RequestMapping(path = arrayOf("/list/future"), method = arrayOf(RequestMethod.GET))
    fun listFuture(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(0)), HttpStatus.OK)
    }

    @RequestMapping(path = arrayOf("/list/past"), method = arrayOf(RequestMethod.GET))
    fun listPast(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(-1)), HttpStatus.OK)
    }

    @RequestMapping(path = arrayOf("/edit/{id}/{targetStatus}"), method = arrayOf(RequestMethod.PATCH))
    fun changeStatus(@PathVariable("id") id: Long,
                     @PathVariable("targetStatus") status: Int): ResponseEntity<String> {
        val sheet = sheetRepository.findById(id).get()
        if (status in 0..2) {
            sheet.status = status
            sheetRepository.save(sheet)
            return ActionResult(true).encode()
        } else {
            return ActionResult(false, HttpStatus.NOT_ACCEPTABLE).encode()
        }
    }

    fun listSheetsWithStatus(status: Int = 1): List<SignUpSheet> {
        return sheetRepository.findByStatus(status)
    }

    @RequestMapping(path = arrayOf("/signup"), method = arrayOf(RequestMethod.POST))
    fun submitSignup(@AuthenticationPrincipal auth: Token,
                     @RequestParam("data") data: String): ResponseEntity<String> {
        // Parse Json
        if (auth.student() == null) {
            return ActionResult(false, HttpStatus.I_AM_A_TEAPOT).encode()
        }
        val student = auth.student()!!
        val obj = JSONObject(data)
        val submitedSheet = obj.getJSONObject("sheet")
        // Check against the declared SignUpSheet, see if there is any missing field
        val sheet = sheetRepository.findById(obj.getLong("id")).get()
        if (!sheet.entries.all { submitedSheet.containsKey(it.eventGroup.id.toString()) }) {
            return ActionResult(false, HttpStatus.NOT_ACCEPTABLE).encode()
        }
        sheet.entries.forEach { group ->
            val selectedOption = submitedSheet.getString(group.eventGroup.id.toString())
            group.eventGroup.events.forEach {
                val recordO = eventRecordRepository.findByEventAndStudent(it, student)
                if (recordO.isPresent) {
                    val r = recordO.get()
                    if (r.checkInTime > 0) {
                        r.signupTime = -1
                        eventRecordRepository.save(r)
                    } else {
                        eventRecordRepository.delete(r)
                    }
                }
            }
            if (selectedOption != "-1") {
                val selectedEvent = eventRepository.findByEventId(eventID = selectedOption).get()
                if (group.eventGroup.events.contains(selectedEvent)) {
                    val recordO = eventRecordRepository.findByEventAndStudent(selectedEvent, student)
                    val eventRecord: ActivityEventRecord
                    if (recordO.isPresent) {
                        eventRecord = recordO.get()
                    } else {
                        eventRecord = ActivityEventRecord()
                        eventRecord.event = selectedEvent
                        eventRecord.student = student
                    }
                    eventRecord.signupTime = System.currentTimeMillis()
                    eventRecordRepository.save(eventRecord)
                }
            }
        }
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/signup/{sheetId}"), method = arrayOf(RequestMethod.GET))
    fun showSignup(@AuthenticationPrincipal auth: Token,
                   @PathVariable sheetId: Long): ResponseEntity<String> {
        if (auth.student() == null) {
            return ActionResult(false, HttpStatus.I_AM_A_TEAPOT).encode()
        }
        val student = auth.student()!!
        val sheet = sheetRepository.findById(sheetId).get()
        val response = JSONObject().put("sheetId", sheet.id.toString())
        val sheetContent = JSONObject()
        sheet.entries.forEach { group ->
            val event = group.eventGroup.events.firstOrNull { event ->
                val fetch = eventRecordRepository.findByEventAndStudent(event, student = student)
                fetch.isPresent && fetch.get().signupTime != -1L
            }
            sheetContent.put(group.eventGroup.id.toString(), event?.eventId ?: "-1")
        }
        return ResponseEntity(response.put("sheet", sheetContent).encode(), HttpStatus.OK)
    }

    @RequestMapping(path = arrayOf("/sendmail"), method= arrayOf(RequestMethod.POST))
    fun sendMail(@RequestParam("students", required = false, defaultValue = "") studentList: String): ResponseEntity<String> {
        val students: List<Student>
        if (studentList.isNotEmpty()) {
            students = ArrayList()
            JSONArray(studentList).forEach { item ->
                if (item is String) {
                    students.add(studentRepository.findByIdNumberIgnoreCase(item).get())
                }
            }
        } else {
            students = studentRepository.findByEmailIsNotNull()
        }
        val tokens : MutableList<MailTemplate> = ArrayList()
        students.forEach {
            val token = emailTokenRepository.save(EmailToken(it))
            val mail = MailTemplateFactory.createTemplateByFileName("tokenNotification")
            mail.recipientAddress = it.email?:""
            mail.setStringValue("lastName", it.lastName)
            mail.setStringValue("firstName", it.firstName)
            mail.setStringValue("token", token.tokenSecret)
            tokens.add(mail)
        }
        Thread(Runnable {
            tokens.forEach {
                println("Sending... ${it.recipientAddress}")
                emailService.sendMail(it, it.recipientAddress)
            }
        }).start()
        return ActionResult(true).encode()
    }


}