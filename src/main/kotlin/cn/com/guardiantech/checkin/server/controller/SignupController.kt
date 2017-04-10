package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONArray
import cn.com.guardiantech.checkin.server.entity.EventGroup
import cn.com.guardiantech.checkin.server.entity.SignUpSheet
import cn.com.guardiantech.checkin.server.httpEntity.ActionResult
import cn.com.guardiantech.checkin.server.repository.EventGroupRepository
import cn.com.guardiantech.checkin.server.repository.SignUpSheetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created by Codetector on 2017/4/10.
 * Project backend
 */
@RestController
@RequestMapping(path = arrayOf("/signup"))
class SignupController {
    @Autowired
    lateinit var sheetRepository: SignUpSheetRepository

    @Autowired
    lateinit var groupRepository: EventGroupRepository

    @PostMapping(path = arrayOf("/create"))
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
        } catch (e: Throwable){
            return ActionResult(e).encode()
        }
    }

    @PostMapping(path = arrayOf("/edit/{id}/add"))
    fun addGroupToSheet(@RequestParam("group") group: Long,
                        @PathVariable("id") sheetId:Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        sht.events.add(grp)
        sheetRepository.save(sht)
        return ActionResult(true).encode()
    }

    @PostMapping(path = arrayOf("/edit/{id}/remove"))
    fun removeGroupFromSheet(@RequestParam("group") group: Long,
                             @PathVariable("id") sheetId:Long): ResponseEntity<String> {
        val grp = groupRepository.findById(group).get()
        val sht = sheetRepository.findById(sheetId).get()
        val result = sht.events.remove(grp)
        sheetRepository.save(sht)
        return ActionResult(result, HttpStatus.NOT_ACCEPTABLE).encode()
    }

    @PostMapping(path = arrayOf("/edit/{id}/set"))
    fun setGroupToSheet(@RequestParam("group") groups: String,
                        @PathVariable("id") sheetId:Long): ResponseEntity<String> {
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
        } catch (e: Throwable){
            return ActionResult(e).encode()
        }
    }

    @GetMapping(path = arrayOf("/list"))
    fun listAll(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", sheetRepository.findAll()), HttpStatus.OK)
    }

    @GetMapping(path = arrayOf("/list/current"))
    fun listOpenSheet(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(1)), HttpStatus.OK)
    }

    @GetMapping(path = arrayOf("/list/future"))
    fun listFuture(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(0)), HttpStatus.OK)
    }

    @GetMapping(path = arrayOf("/list/past"))
    fun listPast(): ResponseEntity<MutableMap<String, List<SignUpSheet>>> {
        return ResponseEntity(Collections.singletonMap("signUps", listSheetsWithStatus(-1)), HttpStatus.OK)
    }

    fun listSheetsWithStatus(status: Int = 1): List<SignUpSheet> {
        return sheetRepository.findByStatus(status)
    }
}