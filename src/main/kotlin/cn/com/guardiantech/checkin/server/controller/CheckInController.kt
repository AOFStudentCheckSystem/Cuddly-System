package cn.com.guardiantech.checkin.server.controller

import abs
import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.authentication.Token
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.ActivityEventRecord
import cn.com.guardiantech.checkin.server.entity.authentication.UserToken
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRecordRepository
import cn.com.guardiantech.checkin.server.repository.EventRepository
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import unitDirection
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Created by Codetector on 2017/4/14.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/checkin"))
class CheckInController {
    private val logger = LoggerFactory.getLogger("cn.com.guardiantech.checkin.service.backup.CheckInRecord")


    @Autowired
    lateinit var recordRepository: EventRecordRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var eventRecordRepository: EventRecordRepository

    @RequestMapping(path = arrayOf("/submit"), method = arrayOf(RequestMethod.PUT, RequestMethod.PATCH))
    fun checkInSubmission(@RequestBody data: String,
                          @AuthenticationPrincipal auth: Token,
                          request: HttpServletRequest): String {
        //Parse Data
        val jsonData = JSONObject(data)
        var isRequestValid = true
        var error: Throwable? = null
        try {
            val event = eventRepository.findByEventId(jsonData.getString("targetEvent")).get()

            if (event.eventStatus > 1) {
                throw IllegalArgumentException("Event has been completed. Invalid request.")
            }

            if (event.eventStatus < 1) {
                event.eventStatus = 1
                eventRepository.save(event)
            }

            val records = jsonData.getJSONArray("recordsToUpload")

            var effectiveUpdate = 0
            var validRecords = 0
            val totalRecords = records.size()
            records.forEach { o ->
                if (o is JSONObject) {
                    validRecords++
                    val recordTimestamp = o.getLong("timestamp")
                    val recordNewStatus = if (o.containsKey("status")) o.getInteger("status") else {
                        1
                    }
                    val targetStudent = studentRepository.findByIdNumberIgnoreCase(o.getString("studentId")).get()
                    val targetEventRecord = recordRepository.findByEventAndStudent(event, targetStudent).orElseGet {
                        val r = ActivityEventRecord()
                        r.event = event
                        r.student = targetStudent
                        r
                    }
                    if (targetEventRecord.checkInTime.abs() <= recordTimestamp.abs()) {
                        effectiveUpdate++
                        targetEventRecord.checkInTime = recordNewStatus.unitDirection() * recordTimestamp
                    }
                    eventRecordRepository.save(targetEventRecord)
                }
            }
            return JSONObject().put("targetEvent", event.eventId).put("totalRecordsReceived", totalRecords).put("validRecords", validRecords).put("effectiveRecords", effectiveUpdate).encode()
        } catch (e: Throwable) {
            isRequestValid = false
            error = e
            throw e
        } finally {
            val username = (auth as? UserToken)?.user?.email ?: "Unknown"
            var ipAddress = request.getHeader("X-FORWARDED-FOR")
            if (ipAddress == null || ipAddress.isBlank()) {
                ipAddress = request.remoteHost.toString()
            }
            logger.info("Request ${if (isRequestValid) "Accepted" else "Rejected"} from $username @ $ipAddress \n $data")
        }
    }

    @RequestMapping(path = arrayOf("/record/{eventId}"), method = arrayOf(RequestMethod.GET))
    fun getRecordForEvent(@PathVariable("eventId") eventId: String): ResponseEntity<Map<String, List<ActivityEventRecord>>> = 
        ResponseEntity(Collections.singletonMap("records", eventRecordRepository.findByEvent(eventRepository.findByEventId(eventId).get())), HttpStatus.OK)

}
