package ru.tenzou.tsodgis.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.tenzou.tsodgis.repository.UserRepository

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api")
class BackendController {

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/usercontent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUserContent(authentication: Authentication): String {
        val user = userRepository.findByUsername(authentication.name).get()
        return "Hello ${user.firstName} ${user.lastName} !"
    }

    @GetMapping("admincontent")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun getAdminContent(authentication: Authentication) = "Admin`s content."

    @GetMapping("/hello")
    fun hello() = "Hello."
}