package ru.tenzou.tsodgis.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import ru.tenzou.tsodgis.dto.request.AuthRequestDto
import ru.tenzou.tsodgis.dto.response.AuthResponseDto
import ru.tenzou.tsodgis.dto.response.UserDto
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.repository.RoleRepository
import ru.tenzou.tsodgis.repository.UserRepository
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AdminRestControllerV1Test @Autowired constructor(
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

    private lateinit var admin: User
    private lateinit var token: String

    private val adminUsername = "admin"
    private val adminPassword = "admin"

    /**
     * Init data base
     */
    @BeforeAll
    fun `create user`() {
        // get roles
        val roleAdmin = roleRepository.findByName("ROLE_ADMIN").get()
        val roleUser = roleRepository.findByName("ROLE_USER").get()

        // create admin
        admin = User(
            adminUsername, "", "", "",
            passwordEncoder.encode(adminPassword),
            listOf(roleAdmin, roleUser)
        )
        admin.status = Status.ACTIVE
        admin.created = Date()
        admin.updated = Date()

        userRepository.save(admin)

        val authRequest = AuthRequestDto(adminUsername, adminPassword)

        val responseJson = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authRequest)
        }
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(responseJson, AuthResponseDto::class.java)
        token = response.token!!
    }

    @Nested
    @DisplayName("Get user")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUser {


        @Test
        fun `should return user`() {
            // given

            // when
            mockMvc.get("/api/v1/admin/user/${admin.id}") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }

            // than
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(UserDto.fromUser(admin)))
                    }
                }
        }

        @Test
        fun `should return No Content`() {
            // given

            // when
            mockMvc.get("/api/v1/admin/user/10") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }

                // than
                .andExpect {
                    status { isNoContent() }
                }
        }
    }
}