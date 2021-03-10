package ru.tenzou.tsodgis.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tenzou.tsodgis.entity.User

interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(name: String): User
}