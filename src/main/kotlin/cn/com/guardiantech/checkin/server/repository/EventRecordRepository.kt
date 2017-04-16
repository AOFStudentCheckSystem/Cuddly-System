package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import cn.com.guardiantech.checkin.server.entity.ActivityEventRecord
import cn.com.guardiantech.checkin.server.entity.Student
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Created by Codetector on 2017/4/7.
 * Project backend
 */
interface EventRecordRepository: CrudRepository<ActivityEventRecord, Long> {
    fun findByStudent(student: Student): List<ActivityEventRecord>
    fun findByEvent(event: ActivityEvent): List<ActivityEventRecord>
    fun findByEventAndStudent(event: ActivityEvent, student: Student): Optional<ActivityEventRecord>
}