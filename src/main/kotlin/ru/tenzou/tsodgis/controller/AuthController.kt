package ru.tenzou.tsodgis.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.jwt.JwtProvider
import ru.tenzou.tsodgis.model.LoginUser
import ru.tenzou.tsodgis.model.NewUser
import ru.tenzou.tsodgis.repository.RoleRepository
import ru.tenzou.tsodgis.repository.UserRepository
import ru.tenzou.tsodgis.web.response.JwtResponse
import ru.tenzou.tsodgis.web.response.ResponseMessage
import java.util.*
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: LoginUser): ResponseEntity<*> {
        val userCandidate: Optional<User> = userRepository.findByUsername(loginRequest.username!!)

        if (userCandidate.isPresent) {
            val user = userCandidate.get()
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication

            val jwt: String = jwtProvider.generateJwtToken(user.username!!)
            val authorities: List<GrantedAuthority> =
                user.roles!!.stream().map { SimpleGrantedAuthority(it.name) }
                    .collect(Collectors.toList<GrantedAuthority>())

            return ResponseEntity.ok(JwtResponse(jwt, user.username, authorities))
        } else {
            return ResponseEntity(ResponseMessage("User not found!"), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody newUser: NewUser): ResponseEntity<*> {
        val userCandidate: Optional<User> = userRepository.findByUsername(newUser.username!!)

        return when {
            userCandidate.isPresent -> ResponseEntity(ResponseMessage("User already exists!"), HttpStatus.BAD_REQUEST)
            usernameExists(newUser.username!!) -> ResponseEntity(ResponseMessage("Username is already taken!"), HttpStatus.BAD_REQUEST)
            emailExists(newUser.email!!) -> ResponseEntity(ResponseMessage("Email is already in user!"), HttpStatus.BAD_REQUEST)
            else -> {
                val user = User(
                    0,
                    newUser.username!!,
                    newUser.firstName!!,
                    newUser.lastName!!,
                    newUser.email!!,
                    encoder.encode(newUser.password),
                    true
                )
                user.roles = listOf(roleRepository.findByName("ROLE_USER"))

                userRepository.save(user)

                ResponseEntity(ResponseMessage("User registered successfully!"), HttpStatus.OK)
            }
        }
    }

    private fun emailExists(email: String): Boolean {
        return userRepository.findByEmail(email).isPresent
    }

    private fun usernameExists(username: String): Boolean {
        return userRepository.findByUsername(username).isPresent
    }
}