package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

    @RequestMapping(path = arrayOf("/remove/{id}"), method = arrayOf(RequestMethod.DELETE))
    fun removeEvent(@PathVariable("id") eventID: String): ResponseEntity<String> {
        return ActionResult(eventRepo.removeByEventId(eventID) > 0).encode()
    }

    @RequestMapping(path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    fun createEvent(@RequestParam("name") name: String,
                    @RequestParam("descriptions", required = false, defaultValue = "") description: String,
                    @RequestParam("time", required = false, defaultValue = "0") time: Long): String {
        val eventDate: Date = if (time == 0L) {
            Date()
        } else {
            Date(time)
        }
        try {
            val evt = eventRepo.save(ActivityEvent(name, eventDate, description))
            return JSONObject().put("success",true).put("newEvent", JSONObject().put("eventId", evt.eventId)).encode()
        } catch (e: Throwable) {
            return ActionResult(e).toString()
        }
    }

    @RequestMapping(path = arrayOf("/list"))
    fun listAllEvents(pageable: Pageable): Page<ActivityEvent> {
//        return eventRepo.findAll(PageRequest(pageable.pageNumber, pageable.pageSize, Sort(Sort.Direction.ASC, "eventTime")))
        return eventRepo.findAll(pageable)
    }

    @RequestMapping(path = arrayOf("/list/pending", "/list/future"), method = arrayOf(RequestMethod.GET))
    fun listFutureEvents() = ResponseEntity<Map<String, Set<ActivityEvent>>>(Collections.singletonMap("events", eventRepo.findByEventStatus(0)), HttpStatus.OK)

    @RequestMapping(path = arrayOf("/list/current", "/list/boarding"), method = arrayOf(RequestMethod.GET))
    fun listCurrentEvents() = ResponseEntity<Map<String, Set<ActivityEvent>>>(Collections.singletonMap("events", eventRepo.findByEventStatus(1)), HttpStatus.OK)

    @RequestMapping(path = arrayOf("/list/past", "/list/completed"), method = arrayOf(RequestMethod.GET))
    fun listPastEvents() = ResponseEntity<Map<String, Set<ActivityEvent>>>(Collections.singletonMap("events", eventRepo.findByEventStatus(2)), HttpStatus.OK)

    @RequestMapping(path = arrayOf("/list/{id}"), method = arrayOf(RequestMethod.GET))
    fun getEventById(@PathVariable id: String): ActivityEvent {
        return eventRepo.findByEventId(id).get()
    }

    @GetMapping(path = arrayOf("/listall"))
    fun listAllEventsNoPage(): Page<ActivityEvent> = listAllEvents(PageRequest(0, Int.MAX_VALUE))

    @RequestMapping(path = arrayOf("/credit"), method = arrayOf(RequestMethod.POST))
    fun creditEvent(@RequestParam("eventId", required = false, defaultValue = "") eventID: String,
                    @RequestParam("time", required = false, defaultValue = "0") newTime: Long,
                    @RequestParam("name", required = true) newName: String,
                    @RequestParam("description", required = false, defaultValue = "") newDescription: String): ResponseEntity<String> {
        val targetEvent: ActivityEvent
        if (eventID.isNotEmpty()) {
            targetEvent = eventRepo.findByEventId(eventID).orElseGet { ActivityEvent(newName) }
        } else {
            targetEvent = ActivityEvent(newName)
        }
        if (newTime != 0L) {
            targetEvent.eventTime = Date(newTime)
        }
        if (newName.isNotEmpty()) {
            targetEvent.eventName = newName
        }
        if (newDescription.isNotEmpty()) {
            targetEvent.eventDescription = newDescription
        }
        eventRepo.save(targetEvent)
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit"), method = arrayOf(RequestMethod.POST))
    fun editEvent(@RequestParam("eventId") eventID: String,
                  @RequestParam("newTime", required = false, defaultValue = "0") newTime: Long,
                  @RequestParam("newName", required = false, defaultValue = "") newName: String,
                  @RequestParam("newDescription", required = false, defaultValue = "") newDescription: String,
                  @RequestParam("newStatus", required = false, defaultValue = "") eventStatus: String): ResponseEntity<String> {
        val eventToEdit = eventRepo.findByEventId(eventID).get()
        if (newTime != 0L) {
            eventToEdit.eventTime = Date(newTime)
        }
        if (newName.isNotEmpty()) {
            eventToEdit.eventName = newName
        }
        if (newDescription.isNotEmpty()) {
            eventToEdit.eventDescription = newDescription
        }
        if (eventStatus.isNotEmpty()) {
            val newStatus = eventStatus.toInt()
            if (newStatus <2 && newStatus >= 0) {
                eventToEdit.eventStatus = newStatus
            }
        }
        eventRepo.save(eventToEdit)
        return ActionResult(true).encode()
    }
}