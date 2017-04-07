package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.PagingAndSortingRepository
import javax.transaction.Transactional

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
interface EventRepository : PagingAndSortingRepository<ActivityEvent, Long>{
    override fun findAll(): MutableList<ActivityEvent>

    @Modifying
    @Transactional
    fun removeByEventId(eventID: String): Long
}