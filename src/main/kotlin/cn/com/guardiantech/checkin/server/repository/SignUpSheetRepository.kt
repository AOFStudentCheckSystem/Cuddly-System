package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.SignUpSheet
import org.springframework.data.repository.CrudRepository

/**
 * Created by Codetector on 2017/4/7.
 * Project backend
 */
interface SignUpSheetRepository : CrudRepository<SignUpSheet, Long> {
    fun findByStatus(status: Int): List<SignUpSheet>
}