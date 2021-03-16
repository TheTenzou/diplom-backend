package ru.tenzou.tsodgis.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val long: Long? = null,
//
//    @CreatedDate
//    @Column(name = "created")
//    var created: Date? = null,
//
//    @LastModifiedDate
//    @Column(name = "updated")
//    var updated: Date? = null,
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status")
//    var status: Status? = null

    @Column(name = "name")
    var name: String,

//    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
//    var users: Collection<User>? = null
) : BaseEntity()