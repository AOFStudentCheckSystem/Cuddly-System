package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@RestController()
@RequestMapping(path = arrayOf("/event"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
class EventController {

    @Autowired
    lateinit var eventRepo: EventRepository

    @RequestMapping(path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    fun createEvent(@RequestParam(name = "name") name: String,
                    @RequestParam(name = "time", required = false, defaultValue = "0") time: Long): String {
        val eventDate:Date = if (time == 0L) { Date() } else { Date(time * 1000) }
        try {
            eventRepo.save(ActivityEvent(name, eventDate))
            return ActionResult(true).toString()
        } catch (e: Throwable) {
            return ActionResult(e).toString()
        }
    }

    @RequestMapping(path = arrayOf("/list"))
    fun listAllEvents(): ResponseEntity<MutableMap<String, MutableList<ActivityEvent>>> {
        return ResponseEntity(Collections.singletonMap("events", eventRepo.findAll()), HttpStatus.OK)
    }
}