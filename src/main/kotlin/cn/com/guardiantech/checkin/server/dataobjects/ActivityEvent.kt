package cn.com.guardiantech.checkin.server.dataobjects

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet


/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@Entity
@Table(name = "events")
class ActivityEvent() {

    constructor(eventName: String): this() {
        this.eventName = eventName
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long = 0

    @Column(unique = true)
    var eventId: String = System.currentTimeMillis().toString(36).toLowerCase()

    lateinit var eventName: String

    var eventTime: Date = Date()

    var eventStatus = 0

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    @JsonBackReference
    @Column(nullable = false)
    var records: MutableSet<ActivityEventRecord> = HashSet()
}