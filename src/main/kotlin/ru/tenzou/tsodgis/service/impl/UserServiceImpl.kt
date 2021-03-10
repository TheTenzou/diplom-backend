package ru.tenzou.tsodgis.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.repository.RoleRepository
import ru.tenzou.tsodgis.repository.UserRepository
import ru.tenzou.tsodgis.service.UserService

@Service
class UserServiceImpl : UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var  passwordEncoder: BCryptPasswordEncoder

    override fun register(user: User): User {
        val roleUser = roleRepository.findByName("ROLE_USER")
        val userRoles = ArrayList<Role>()
        userRoles.add(roleUser)

        user.password = passwordEncoder.encode(user.password)
        user.roles = userRoles

        user.status = Status.ACTIVE

        val registeredUser = userRepository.save(user)

        return registeredUser
    }

    override fun getAll(): List<User> = userRepository.findAll()

    override fun findByUsername(username: String): User = userRepository.findByUsername(username)

    override fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    override fun delete(id: Long) {
        userRepository.deleteById(id)
    }
}