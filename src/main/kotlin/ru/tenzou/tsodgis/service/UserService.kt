package ru.tenzou.tsodgis.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import ru.tenzou.tsodgis.entity.User

interface UserService {

    fun register(user: User): User

    fun getAll(pageable: Pageable): Page<User>

    fun findByUsername(username: String): User?

    fun findById(id: Long): User?

    fun delete(id: Long)
}