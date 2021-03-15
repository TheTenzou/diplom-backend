package ru.tenzou.tsodgis.security.jwt.user

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.tenzou.tsodgis.service.UserService

@Service
class JwtUserDetailsService @Autowired constructor(private val userService: UserService) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = username?.let { userService.findByUsername(it) }
            ?: throw UsernameNotFoundException("User with username: $username not found")

        logger.info("IN loadUserByUsername - user with username: $username successfully loaded")
        return JwtUserDetails.create(user)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }
}