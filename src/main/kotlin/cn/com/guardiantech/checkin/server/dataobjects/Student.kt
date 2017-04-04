package cn.com.guardiantech.checkin.server.dataobjects

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@Entity
class Student {
    @Id
    @JsonIgnore
    @GeneratedValue
    var id: Long = 0

    @Column(unique = true, length = 16, nullable = false)
    lateinit var idNumber: String

    @Column(nullable = false, length = 64)
    lateinit var lastName: String

    @Column(nullable = false, length = 64)
    lateinit var firstName: String

    @Column(length = 64)
    lateinit var preferredName: String

    @Column(unique = true, length = 64)
    lateinit var cardSecret: String
}