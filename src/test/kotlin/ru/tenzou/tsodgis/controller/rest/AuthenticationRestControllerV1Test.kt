package ru.tenzou.tsodgis.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.tenzou.tsodgis.dto.request.AuthRequestDto
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.repository.RoleRepository
import ru.tenzou.tsodgis.repository.UserRepository
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
internal class AuthenticationRestControllerV1Test @Autowired constructor(
    private val mockMvc: MockMvc,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    companion object {
        @Container
        val container = PostgreSQLContainer<Nothing>("postgres:12").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.liquibase.url", container::getJdbcUrl)
            registry.add("spring.liquibase.user", container::getUsername)
            registry.add("spring.liquibase.password", container::getPassword)
        }
    }

    @Test
    fun `login with good credential`() {
        // request
        val authRequest = AuthRequestDto("admin", "admin")
        // create user

        val roleAdmin = roleRepository.findByName("ROLE_ADMIN").get()
        val roleUser = roleRepository.findByName("ROLE_USER").get()

        val user = User(
            authRequest.username, "", "", "",
            passwordEncoder.encode(authRequest.password),
            listOf(roleAdmin, roleUser)
        )

        user.status = Status.ACTIVE
        user.created = Date()
        user.updated = Date()

        userRepository.save(user)

        // send request
        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authRequest)
        }
            .andDo { print() }

        // insertion
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") { value(user.username) }
                jsonPath("$.token") { exists() }
            }
    }

    @Test
    fun `login with bad credential`() {
        // request
        val authRequest = AuthRequestDto("test", "wrong_pass")
        // create user
        val user = User(
            authRequest.username, "", "", "",
            passwordEncoder.encode("pass"),
            listOf(Role("ROLE_ADMIN"), Role("ROLE_USER"))
        )
        user.status = Status.ACTIVE

        // mock userService
//        every { userService.findByUsername(user.username!!) } returns user

        // send request
        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authRequest)
        }
            .andDo { print() }
        // insertion
            .andExpect {
                status { isUnauthorized() }
            }
    }
}