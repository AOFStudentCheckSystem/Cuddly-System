package cn.com.guardiantech.checkin.server.controller

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
                    @RequestParam("descriptions", required = false, defaultValue = "") description: String,
                    @RequestParam("time", required = false, defaultValue = "0") time: Long): String {
        val eventDate: Date = if (time == 0L) {
            Date()
        } else {
            Date(time)
        }
        try {
            eventRepo.save(ActivityEvent(name, eventDate, description))
            return ActionResult(true).toString()
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
    fun listFutureEvents() = eventRepo.findByEventStatus(0)

    @RequestMapping(path = arrayOf("/list/current", "/list/boarding"), method = arrayOf(RequestMethod.GET))
    fun listCurrentEvents() = eventRepo.findByEventStatus(1)

    @RequestMapping(path = arrayOf("/list/past", "/list/completed"), method = arrayOf(RequestMethod.GET))
    fun listPastEvents() = eventRepo.findByEventStatus(2)

    @RequestMapping(path = arrayOf("/list/{id}"), method = arrayOf(RequestMethod.GET))
    fun getEventById(@PathVariable id: String): ActivityEvent {
        return eventRepo.findByEventId(id).get()
    }

    @GetMapping(path = arrayOf("/listall"))
    fun listAllEventsNoPage(): Page<ActivityEvent> = listAllEvents(PageRequest(0, Int.MAX_VALUE))

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
            eventToEdit.eventStatus = eventStatus.toInt()
        }
        eventRepo.save(eventToEdit)
        return ActionResult(true).encode()
    }
}