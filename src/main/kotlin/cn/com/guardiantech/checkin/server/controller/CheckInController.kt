package cn.com.guardiantech.checkin.server.controller

import abs
import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.ActivityEventRecord
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRecordRepository
import cn.com.guardiantech.checkin.server.repository.EventRepository
import cn.com.guardiantech.checkin.server.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unitDirection
import java.util.*

/**
 * Created by Codetector on 2017/4/14.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/checkin"))
class CheckInController {
    @Autowired
    lateinit var recordRepository: EventRecordRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var eventRecordRepository: EventRecordRepository

    @RequestMapping(path = arrayOf("/submit"), method = arrayOf(RequestMethod.PUT, RequestMethod.PATCH))
    fun checkInSubmission(@RequestBody data: String): String {
        //Parse Data
        val jsonData = JSONObject(data)
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
                validRecords ++
                val recordTimestamp = o.getLong("timestamp")
                val recordNewStatus = o.getInteger("status")
                val targetStudent = studentRepository.findByIdNumberIgnoreCase(o.getString("studentId")).get()
                val targetEventRecord = recordRepository.findByEventAndStudent(event, targetStudent).orElseGet {
                    val r = ActivityEventRecord()
                    r.event = event
                    r.student = targetStudent
                    r
                }
                if (targetEventRecord.checkInTime.abs() <= recordTimestamp) {
                    effectiveUpdate++
                    targetEventRecord.checkInTime = recordNewStatus.unitDirection() * recordTimestamp
                }
                eventRecordRepository.save(targetEventRecord)
            }
        }
        return JSONObject().put("targetEvent", event.eventId).put("totalRecordsReceived", totalRecords).put("validRecords", validRecords).put("effectiveRecords", effectiveUpdate).encode()
    }

    @RequestMapping(path = arrayOf("/record/{eventId}"), method = arrayOf(RequestMethod.GET))
    fun getRecordForEvent(@PathVariable("eventId") eventId: String): ResponseEntity<Map<String, List<ActivityEventRecord>>> = 
        ResponseEntity(Collections.singletonMap("records", eventRecordRepository.findByEvent(eventRepository.findByEventId(eventId).get())), HttpStatus.OK)

}
