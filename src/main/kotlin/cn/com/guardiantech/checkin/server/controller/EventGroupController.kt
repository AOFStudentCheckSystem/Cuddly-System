package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONArray
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.EventGroup
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventGroupRepository
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@RestController()
@RequestMapping(path = arrayOf("/event/group"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class EventGroupController {

    @Autowired
    lateinit var eventRepo: EventRepository

    @Autowired
    lateinit var eventGroupRepo: EventGroupRepository

    @RequestMapping(path = arrayOf("/new"), method = arrayOf(RequestMethod.POST))
    fun createGroup(@RequestParam("name") name: String,
                    @RequestParam("groupItems", required = false, defaultValue = "[]") items: String): ResponseEntity<String> {
        val eventIds = JSONArray(items)
        val events: MutableList<ActivityEvent> = ArrayList()
        eventIds.forEach {
            events.add(eventRepo.findByEventId(it.toString()).get())
        }
        val group = eventGroupRepo.save(EventGroup(name, events = events.toHashSet()))
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{groupId}/add"), method = arrayOf(RequestMethod.POST))
    fun addEventToGroup(@RequestParam("eventId") eventId: String,
                        @PathVariable("groupId") groupId: Long): ResponseEntity<String> {
        val targetGroup = eventGroupRepo.findById(groupId).get()
        val targetEvent = eventRepo.findByEventId(eventId).get()
        targetGroup.events.add(targetEvent)
        eventGroupRepo.save(targetGroup)
        return ActionResult(success = true).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{groupId}/remove"), method = arrayOf(RequestMethod.POST))
    fun removeEventFromGroup(@RequestParam("eventId") eventId: String,
                             @PathVariable("groupId") groupId: Long): ResponseEntity<String> {
        val targetGroup = eventGroupRepo.findById(groupId).get()
        val targetEvent = eventRepo.findByEventId(eventId).get()
        val result = targetGroup.events.remove(targetEvent)
        eventGroupRepo.save(targetGroup)
        return ActionResult(result).encode()
    }

    @RequestMapping(path = arrayOf("/edit/{groupId}/set"), method = arrayOf(RequestMethod.POST))
    fun setEventToGroup(@RequestParam("eventList") items: String,
                        @PathVariable("groupId") groupId: Long): ResponseEntity<String> {
        val targetGroup = eventGroupRepo.findById(groupId).get()
        val eventIds = JSONArray(items)
        val events: MutableList<ActivityEvent> = ArrayList()
        eventIds.forEach {
            events.add(eventRepo.findByEventId(it.toString()).get())
        }
        targetGroup.events.clear()
        targetGroup.events.addAll(events)
        eventGroupRepo.save(targetGroup)
        return ActionResult(success = true).encode()
    }

    @RequestMapping(path = arrayOf("/remove/{id}"), method = arrayOf(RequestMethod.DELETE))
    fun removeEventGroupById(@PathVariable id:Long): ResponseEntity<String> {
        return ActionResult(eventGroupRepo.removeById(id) > 0).encode()
    }

    @RequestMapping(path = arrayOf("/list"))
    fun listEventGroupss(pageable: Pageable): Page<EventGroup> = eventGroupRepo.findAllByOrderByIdDesc(pageable)

    @GetMapping(path = arrayOf("/listall"))
    fun listAllEventGroups() = listEventGroupss(PageRequest(0, Int.MAX_VALUE))
}