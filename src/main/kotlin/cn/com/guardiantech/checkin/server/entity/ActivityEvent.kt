package cn.com.guardiantech.checkin.server.entity

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

    constructor(eventName: String, eventTime: Date) : this(eventName) {
        this.eventTime = eventTime
    }

    constructor(eventName: String, eventTime: Date, eventdescription: String): this(eventName, eventTime) {
        this.eventDescription = eventdescription
    }

    @Id
    @GeneratedValue
    var id: Int = 0

    @Column(unique = true)
    var eventId: String = System.currentTimeMillis().toString(36).toLowerCase()

    lateinit var eventName: String

    @Lob
    var eventDescription: String = ""

    var eventTime: Date = Date()

    var eventStatus = 0

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    @JsonBackReference
    @Column(nullable = false)
    var records: MutableSet<ActivityEventRecord> = HashSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ActivityEvent

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}