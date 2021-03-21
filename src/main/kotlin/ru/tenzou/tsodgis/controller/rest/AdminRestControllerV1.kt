package ru.tenzou.tsodgis.controller.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.tenzou.tsodgis.dto.response.UserDto
import ru.tenzou.tsodgis.entity.User
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

    class Response(
        val result: MutableList<User>,
        val currentPage: Int,
        val totalItems: Long,
        val totalPages: Int
    )

    @GetMapping("users")
    fun getUsersList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int
    ): ResponseEntity<Response> {
        val pageable: Pageable = PageRequest.of(page, size)

        val resultPage = userService.getAll(pageable)

        return ResponseEntity(
            Response(
                resultPage.content,
                resultPage.number,
                resultPage.totalElements,
                resultPage.totalPages
            ), HttpStatus.OK
        )
    }

}