package ru.tenzou.tsodgis.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
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
//    var status: Status? = null,

    @Column(name = "username")
    var username: String? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "password")
    var password: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Collection<Role>? = null
) : BaseEntity()