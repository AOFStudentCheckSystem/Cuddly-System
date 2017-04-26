package cn.com.guardiantech.checkin.server.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@Entity
@Table(name = "event_records", uniqueConstraints = arrayOf(UniqueConstraint(name = "unique_record_event", columnNames = arrayOf("event", "student"))))
class ActivityEventRecord {

    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long = 0

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "student")
    lateinit var student: Student

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "event")
    lateinit var event: ActivityEvent

    var signupTime: Long = -1

    var checkInTime: Long = -1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ActivityEventRecord

        if (student != other.student) return false
        if (event != other.event) return false

        return true
    }

    override fun hashCode(): Int {
        var result = student.hashCode()
        result = 31 * result + event.hashCode()
        return result
    }
}