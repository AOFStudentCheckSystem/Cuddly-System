package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.EventGroup
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.transaction.Transactional

/**
 * Created by Codetector on 2017/4/7.
 * Project backend
 */
interface EventGroupRepository: CrudRepository<EventGroup, Long> {
    fun findById(id: Long): Optional<EventGroup>

    @Modifying
    @Transactional
    fun removeById(id: Long): Long
}