package ru.tenzou.tsodgis.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import ru.tenzou.tsodgis.dto.AuthRequestDto
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import ru.tenzou.tsodgis.security.jwt.JwtTokenProvider
import ru.tenzou.tsodgis.service.UserService

@SpringBootTest
@AutoConfigureMockMvc
internal class AuthenticationRestControllerV1Test @Autowired constructor(
    val mockMvc: MockMvc,
    val userService: UserService,
    val objectMapper: ObjectMapper,
    val passwordEncoder: BCryptPasswordEncoder,
    val tokenProvider: JwtTokenProvider
) {
    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun userService() = mockk<UserService>()
    }

    @Test
    fun `should return jwt`() {
        val authRequest = AuthRequestDto("test", "pass")
        val user = User(
            authRequest.username, "", "", "",
            passwordEncoder.encode(authRequest.password),
            listOf(Role("ROLE_ADMIN"), Role("ROLE_USER"))
        )
        user.status = Status.ACTIVE

        every { userService.findByUsername(user.username!!) } returns user

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authRequest)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") { value(user.username) }
                jsonPath("$.token") { exists() }
            }
    }
}