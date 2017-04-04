package cn.com.guardiantech.checkin.server.dataobjects

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
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

    @OneToOne(optional = false, orphanRemoval = true )
    @JoinColumn(name = "student")
    lateinit var student: Student

    @JsonManagedReference
    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "event")
    lateinit var event: ActivityEvent
}