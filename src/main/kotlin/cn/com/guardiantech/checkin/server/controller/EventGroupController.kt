package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONArray
import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.EventGroup
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventGroupRepository
import cn.com.guardiantech.checkin.server.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
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
        val eventIds  = JSONArray(items)
        val events: MutableList<ActivityEvent> = ArrayList()
        eventIds.forEach {
            events.add(eventRepo.findByEventId(it.toString()).get())
        }
        val group = eventGroupRepo.save(EventGroup(name, events = events.toHashSet()))
        return ActionResult(true).encode()
    }

    @RequestMapping(path = arrayOf("/list"))
    fun listAllEvents(): ResponseEntity<MutableMap<String, MutableList<ActivityEvent>>> {
        return ResponseEntity(Collections.singletonMap("events", eventRepo.findAll()), HttpStatus.OK)
    }


}