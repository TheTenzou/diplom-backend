package ru.tenzou.tsodgis.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*

/**
 * User role
 */
@Entity
@Table(name = "roles")
data class Role(

    @Column(name = "name")
    var name: String,

//    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
//    var users: Collection<User>? = null
) : BaseEntity()