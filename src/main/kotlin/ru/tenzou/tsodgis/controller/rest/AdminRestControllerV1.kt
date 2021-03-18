package ru.tenzou.tsodgis.controller.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tenzou.tsodgis.dto.response.UserDto
import ru.tenzou.tsodgis.service.UserService

@RestController
@RequestMapping("/api/v1/admin")
class AdminRestControllerV1 @Autowired constructor(
    private val userService: UserService
) {

    @GetMapping("user/{id}")
    fun getUserById(@PathVariable(name = "id") id: Long): ResponseEntity<UserDto> {
        val user = userService.findById(id)

        user?.let { return ResponseEntity.ok(UserDto.fromUser(it)) }
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}