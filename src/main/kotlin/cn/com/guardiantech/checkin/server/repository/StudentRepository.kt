package cn.com.guardiantech.checkin.server.repository

import cn.com.guardiantech.checkin.server.entity.Student
import cn.com.guardiantech.checkin.server.entity.authentication.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

/**
 * Created by Codetector on 2017/4/6.
 * Project backend
 */
interface StudentRepository: PagingAndSortingRepository<Student, Long> {
    fun findByIdNumberIgnoreCase(idNumber: String): Optional<Student>

    fun findByCardSecretIgnoreCase(card: String): Optional<Student>

    fun findByEmailIgnoreCase(email: String): Optional<Student>

    override fun findAll(p0: Pageable): Page<Student>
}