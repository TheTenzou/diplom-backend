package ru.tenzou.tsodgis.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*

/**
 * Base entity
 */
@MappedSuperclass
open class BaseEntity(

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    /**
     * record create date
     */
    @CreatedDate
    @Column(name = "created")
    var created: Date? = null,

    /**
     * record last modification date
     */
    @LastModifiedDate
    @Column(name = "updated")
    var updated: Date? = null,

    /**
     * row consider to be deleted if status equal to <code> Status.DELETED </code>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: Status? = null
)
