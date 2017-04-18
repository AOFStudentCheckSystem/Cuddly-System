package cn.com.guardiantech.checkin.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("event_group","signup_sheet"))))
class SignupSheetEntry() {
    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long = 0

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_group")
    lateinit var eventGroup: EventGroup

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "signup_sheet")
    lateinit var sheet: SignUpSheet

    var weight = 0 //Heavy objects float on top!!!

    constructor(eventGroup: EventGroup, sheet: SignUpSheet, weight: Int = 0):this() {
        this.eventGroup = eventGroup
        this.sheet = sheet
        this.weight = weight
    }
}