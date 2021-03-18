package ru.tenzou.tsodgis.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ru.tenzou.tsodgis.entity.User

/**
 * User data transfer object
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(
    var id: Long?,
    var username: String?,
    var firstName: String?,
    var lastName: String?,
    var email: String?
) {
    fun toUser(): User {
        val user = User()
        user.id = this.id
        user.username = this.username
        user.firstName = this.firstName
        user.lastName = this.lastName
        user.email = this.email

        return user
    }

    companion object {
        fun fromUser(user: User) = UserDto(user.id, user.username, user.firstName, user.lastName, user.email)
    }
}

