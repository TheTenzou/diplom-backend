package ru.tenzou.tsodgis.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.tenzou.tsodgis.entity.User
import java.util.*

interface UserRepository : JpaRepository<User, Long> {

    override fun findAll(pageable: Pageable): Page<User>

    fun findByUsername(name: String): Optional<User>
}