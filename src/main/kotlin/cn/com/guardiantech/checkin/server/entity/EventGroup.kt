package cn.com.guardiantech.checkin.server.entity

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/7.
 * Project backend
 */
@Entity
class EventGroup() {
    constructor(name: String): this() {
        this.name = name
    }

    constructor(name: String, events: MutableSet<ActivityEvent>): this(name) {
        this.events = events
    }

    @Id
    @GeneratedValue
    var id: Long = 0

    var name: String = ""

    @ManyToMany(cascade = arrayOf(CascadeType.DETACH))
    var events: MutableSet<ActivityEvent> = hashSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as EventGroup

        if (id != other.id) return false

        return true
    }
}