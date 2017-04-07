package cn.com.guardiantech.checkin.server.entity

import org.hibernate.annotations.OnDelete
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany

/**
 * Created by Codetector on 2017/4/7.
 * Project backend
 */
@Entity
class SignUpSheet() {
    constructor(name: String):this() {
        this.name = name
    }

    @Id
    @GeneratedValue
    var id: Long = 0

    var status: Int = 0

    lateinit var name: String

    @ManyToMany
    var events:Set<EventGroup> = emptySet()
}