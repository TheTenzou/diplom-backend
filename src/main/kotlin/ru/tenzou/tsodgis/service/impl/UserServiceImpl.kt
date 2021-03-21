package ru.tenzou.tsodgis.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.repository.RoleRepository
import ru.tenzou.tsodgis.repository.UserRepository
import ru.tenzou.tsodgis.service.UserService

/**
 * user service
 */
@Service
class UserServiceImpl : UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var  passwordEncoder: BCryptPasswordEncoder

    override fun register(user: User): User {
        val roleUser = roleRepository.findByName("ROLE_USER").get()
        val userRoles = ArrayList<Role>()
        userRoles.add(roleUser)

        user.password = passwordEncoder.encode(user.password)
        user.roles = userRoles

        user.status = Status.ACTIVE

        val registeredUser = userRepository.save(user)

        logger.info("IN register - user: $registeredUser successfully registered")

        return registeredUser
    }

    override fun getAll(pageable: Pageable): Page<User> {
        val result = userRepository.findAll(pageable)
        logger.info("IN getAll - ${result.totalElements} users found")
        return result
    }

    override fun findByUsername(username: String): User? {
        val result = userRepository.findByUsername(username).orElse(null)
        logger.info("IN findByUsername - user: $result found by username $username")
        return result
    }

    override fun findById(id: Long): User? {
        val result = userRepository.findById(id).orElse(null)
        if (result == null) {
            logger.warn("IN findById - no user found by id: $id")
        }
        logger.info("IN findByUsername - user $result found by id $id")
        return result
    }

    override fun delete(id: Long) {
        userRepository.deleteById(id)
        logger.info("In delete - user with id $id successfully")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}