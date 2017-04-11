package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.User
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
interface StudentRepository: CrudRepository<Student, Long> {
    fun findByIdNumberIgnoreCase(idNumber: String): Optional<Student>
}