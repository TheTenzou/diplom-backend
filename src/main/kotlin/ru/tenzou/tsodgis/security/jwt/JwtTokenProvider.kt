package ru.tenzou.tsodgis.security.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.security.jwt.exception.JwtAuthenticationException
import java.util.*
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

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

    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUsername(token: String): String = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.drop(7)
        }
        return null
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (e: JwtException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        }
    }

    private fun getRoleNames(userRoles: Collection<Role>) =
        userRoles.stream().map { it.name }.collect(Collectors.toList())
}