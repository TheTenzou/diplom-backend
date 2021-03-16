package ru.tenzou.tsodgis.security.jwt

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        try {
            val exception = request.getAttribute("exception") as Exception
            response.writer.println(exception.toString())
            logger.error(exception.toString())
        } catch (e: Exception) {

            val message =
                if (authException.cause != null) {
                    authException.cause.toString() + " " + authException.message.toString()
                } else {
                    authException.message.toString()
                }
            logger.error(message)
            response.writer.println(message)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)
    }
}