package cn.com.guardiantech.checkin.server.entity

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
class Student() {
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
    var email: String? = null

    @Column(unique = true, length = 64)
    var cardSecret: String? = null

    constructor(idNumber: String, lastName: String, firstName: String, preferredName: String): this() {
        this.idNumber = idNumber
        this.lastName = lastName
        this.firstName = firstName
        this.preferredName = preferredName
    }

    constructor(idNumber: String, lastName: String, firstName: String, preferredName: String, email: String): this(idNumber, lastName, firstName, preferredName) {
        this.email = email.toLowerCase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Student

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}