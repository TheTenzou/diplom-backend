package ru.tenzou.tsodgis.controller.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tenzou.tsodgis.dto.AuthRequestDto
import ru.tenzou.tsodgis.dto.AuthResponseDto
import ru.tenzou.tsodgis.security.jwt.JwtTokenProvider
import ru.tenzou.tsodgis.service.UserService

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationRestControllerV1 @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {
    fun login(@RequestBody requestDto: AuthRequestDto): AuthResponseDto {
        try {
            val username = requestDto.username
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, requestDto.password))
            val user = username?.let { userService.findByUsername(it) }
                ?: throw UsernameNotFoundException("User with username: $username not found")

            val token = user.roles?.let { jwtTokenProvider.createToken(username, it) }

            return AuthResponseDto(username, token)
        } catch (e: AuthenticationException) {
            throw BadCredentialsException("Invalid username or password")
        }
    }
}