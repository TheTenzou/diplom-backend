package ru.tenzou.tsodgis.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tenzou.tsodgis.entity.Role
import java.util.*

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Optional<Role>
}