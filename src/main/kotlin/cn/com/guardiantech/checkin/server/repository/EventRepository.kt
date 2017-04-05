package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.ActivityEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
interface EventRepository : PagingAndSortingRepository<ActivityEvent, Long>{
    override fun findAll(): MutableList<ActivityEvent>
}