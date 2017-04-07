package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@RestController()
@RequestMapping(path = arrayOf("/event"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class EventController {

    @Autowired
    lateinit var eventRepo: EventRepository

    @RequestMapping(path = arrayOf("/remove/{id}"))
    fun removeEvent(@PathVariable("id") eventID: String): ResponseEntity<String> {
        return ActionResult(eventRepo.removeByEventId(eventID) > 0).encode()
    }

    @RequestMapping(path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    fun createEvent(@RequestParam("name") name: String,
                    @RequestParam("time", required = false, defaultValue = "0") time: Long): String {
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