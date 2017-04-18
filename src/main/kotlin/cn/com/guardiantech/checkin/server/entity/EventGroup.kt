package cn.com.guardiantech.checkin.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
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

    @ManyToMany(fetch = FetchType.LAZY)
    var events: MutableSet<ActivityEvent> = hashSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "eventGroup")
    @JsonBackReference
    var inEntries: MutableSet<SignupSheetEntry> = hashSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as EventGroup

        if (id != other.id) return false

        return true
    }
}