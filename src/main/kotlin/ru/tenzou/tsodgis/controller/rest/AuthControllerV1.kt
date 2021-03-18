package ru.tenzou.tsodgis.controller.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import ru.tenzou.tsodgis.dto.request.AuthRequestDto
import ru.tenzou.tsodgis.dto.response.AuthResponseDto
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.security.jwt.JwtTokenProvider
import ru.tenzou.tsodgis.service.UserService

/**
 * Authentication REST controller v.1
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthControllerV1 @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(@RequestBody requestDto: AuthRequestDto): ResponseEntity<AuthResponseDto> {
        try {
            val username: String = requestDto.username ?: throw BadCredentialsException("No username")
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, requestDto.password))
            val user: User = userService.findByUsername(username)
                ?: throw UsernameNotFoundException("User with username: $username not found")

            val token =
                jwtTokenProvider.createToken(
                    username,
                    user.roles ?: throw BadCredentialsException("User don't have privileges")
                )

            return ResponseEntity.ok(AuthResponseDto(username, token))
        } catch (e: AuthenticationException) {
            throw BadCredentialsException("Invalid username or password")
        }
    }
}