package ru.tenzou.tsodgis.security.jwt

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import ru.tenzou.tsodgis.entity.Role
import java.util.*
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

/**
 * Provide utility functions for jwt interactions
 */
@Component
class JwtTokenProvider {

    @Value("\${jwt.token.secret}")
    private lateinit var secret: String

    @Value("\${jwt.token.expired}")
    private var validityInMs: Int = 0

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @PostConstruct
    protected fun init() {
        secret = Base64.getEncoder().encodeToString(secret.toByteArray())
    }

    /**
     * create token based on user an roles
     */
    fun createToken(username: String, role: Collection<Role>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = getRoleNames(role)

        val now = Date()
        val validity = Date(now.time + validityInMs)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    /**
     * get authentication by token
     */
    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    /**
     * parse user name
     */
    fun getUsername(token: String): String = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject

    /**
     * get token
     */
    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.drop(7)
        }
        return null
    }

    /**
     * check if token is valid
     */
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid Jwt signature: ${e.message}")
        } catch (e: MalformedJwtException) {
            logger.error("Invalid Jwt token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            logger.error("Jwt token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            logger.error("Jwt token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.error("Jwt claims string is empty: ${e.message}")
        }
        return false
    }

    /**
     * parse roles
     */
    private fun getRoleNames(userRoles: Collection<Role>) =
        userRoles.stream().map { it.name }.collect(Collectors.toList())

    companion object {
        private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    }
}