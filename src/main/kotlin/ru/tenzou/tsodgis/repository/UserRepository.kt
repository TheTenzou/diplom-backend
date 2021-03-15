package ru.tenzou.tsodgis.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tenzou.tsodgis.entity.User
import java.util.*

interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(name: String): Optional<User>
}