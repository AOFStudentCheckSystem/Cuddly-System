package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONArray
import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.ActivityEventRecord
import cn.com.guardiantech.checkin.server.entity.EventGroup
import cn.com.guardiantech.checkin.server.entity.SignUpSheet
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventGroupRepository
import cn.com.guardiantech.checkin.server.repository.EventRecordRepository
import cn.com.guardiantech.checkin.server.repository.EventRepository
import cn.com.guardiantech.checkin.server.repository.SignUpSheetRepository
import com.sun.deploy.net.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created by Codetector on 2017/4/10.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/signup"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class SignupController {
    @Autowired
    lateinit var sheetRepository: SignUpSheetRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var eventRecordRepository: EventRecordRepository

    @Autowired
    lateinit var groupRepository: EventGroupRepository

    @RequestMapping(path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    fun createSheet(@RequestParam("name") sheetName: String,
                    @RequestParam("groups", required = false, defaultValue = "[]") groups: String): ResponseEntity<String> {
        try {
            val jsonArrayGroup = JSONArray(groups)
            val eventGroupList: MutableList<EventGroup> = arrayListOf()
            jsonArrayGroup.forEach {
                eventGroupList.add(groupRepository.findById(it.toString().toLong()).get())
            }
            val sheet = SignUpSheet(sheetName)
            sheet.events.addAll(eventGroupList)
            sheetRepository.save(sheet)
            return ActionResult(true).encode()
        } catch (e: Throwable) {
            return ActionResult(e).encode()
        }
    }

    @RequestMapping(path = arrayOf("/edit/{id}/add"), method = arrayOf(RequestMethod.POST))
    fun addGroupToSheet(@RequestParam("group") group: Long,
                        @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        sht.events.add(grp)
        sheetRepository.save(sht)
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{id}/remove"), method = arrayOf(RequestMethod.POST))
    fun removeGroupFromSheet(@RequestParam("group") group: Long,
                             @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        val result = sht.events.remove(grp)
        sheetRepository.save(sht)
        return ActionResult(result, HttpStatus.NOT_ACCEPTABLE).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{id}/set"), method = arrayOf(RequestMethod.POST))
    fun setGroupToSheet(@RequestParam("group") groups: String,
                        @PathVariable("id") sheetId: Long): ResponseEntity<String> {
        try {
            val jsonArrayGroup = JSONArray(groups)
            val eventGroupList: MutableList<EventGroup> = arrayListOf()
            jsonArrayGroup.forEach {
                eventGroupList.add(groupRepository.findById(it.toString().toLong()).get())
            }
            val sheet = sheetRepository.findById(sheetId).get()
            sheet.events.clear()
            sheet.events.addAll(eventGroupList)
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
        if (!sheet.events.all { submitedSheet.containsKey(it.id.toString()) }) {
            return ActionResult(false, HttpStatus.NOT_ACCEPTABLE).encode()
        }
        sheet.events.forEach { group ->
            val selectedOption = submitedSheet.getString(group.id.toString())
            val selectedEvent = eventRepository.findByEventId(eventID = selectedOption).get()
            if (group.events.contains(selectedEvent)) {
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
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{id}"), method = arrayOf(RequestMethod.POST))
    fun editSignup(@PathVariable("id") id: Long,
                   @RequestParam("newName") newName: String): ResponseEntity<String> {
        val target = sheetRepository.findById(id).get()
        target.name = newName
        sheetRepository.save(target)
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
        sheet.events.forEach { group ->
            val event = group.events.firstOrNull { event ->
                val fetch = eventRecordRepository.findByEventAndStudent(event, student = student)
                fetch.isPresent && fetch.get().signupTime != -1L
            }
            sheetContent.put(group.id.toString(), event?.eventId ?: "-1")
        }
        return ResponseEntity(response.put("sheet", sheetContent).encode(), HttpStatus.OK)
    }
}