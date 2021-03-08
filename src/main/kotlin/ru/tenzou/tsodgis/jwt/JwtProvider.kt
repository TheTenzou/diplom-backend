package ru.tenzou.tsodgis.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.tenzou.tsodgis.repository.UserRepository
import java.lang.IllegalArgumentException
import java.util.*

@Component
class JwtProvider {

    private val logger: Logger = LoggerFactory.getLogger(JwtProvider::class.java)

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${assm.app.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${assm.app.jwtExpiration}")
    var jwtExpiration: Int? = 0

    fun generateJwtToken(username: String): String =
        Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().getTime() + jwtExpiration!! * 1000))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact()

    fun validateJwtToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature -> Message: {}", e)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token -> Message: {}", e)
        } catch (e: ExpiredJwtException) {
            logger.error("Expired JWT token -> Message: {}", e)
        } catch (e: UnsupportedJwtException) {
            logger.error("Unsupported JWT token -> Message: {}" , e)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empy -> Message: {}", e)
        }

        return false
    }

    fun getUserNameFromJwtToken(token: String): String =
        Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
            .subject
}