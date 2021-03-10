package ru.tenzou.tsodgis.service

import ru.tenzou.tsodgis.entity.User

interface UserService {

    fun register(user: User): User

    fun getAll(): List<User>

    fun findByUsername(username: String): User

    fun findById(id: Long): User?

    fun delete(id: Long)
}