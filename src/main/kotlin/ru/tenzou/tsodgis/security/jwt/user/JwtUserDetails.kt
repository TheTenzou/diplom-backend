package ru.tenzou.tsodgis.security.jwt.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.tenzou.tsodgis.entity.Role
import ru.tenzou.tsodgis.entity.Status
import ru.tenzou.tsodgis.entity.User
import java.util.*
import java.util.stream.Collectors

class JwtUserDetails(
    @JsonIgnore
    val id: Long?,
    private val username: String?,
    val firstName: String?,
    val lastName: String?,
    @JsonIgnore
    private val password: String?,
    val email: String?,
    private val enabled: Boolean?,
    @JsonIgnore
    val lastPasswordResetDate: Date?,
    private val authority: MutableCollection<out GrantedAuthority>?,
) : UserDetails {
    
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? = authority

    override fun getPassword() = password

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled(): Boolean = enabled == true

    companion object {
        fun create(user: User) = JwtUserDetails(
            user.id,
            user.username,
            user.firstName,
            user.lastName,
            user.password,
            user.email,
            user.status == Status.ACTIVE,
            user.updated,
            mapToGrantedAuthorities(user.roles)
        )

        private fun mapToGrantedAuthorities(userRoles: Collection<Role>?) =
            userRoles?.stream()?.map { SimpleGrantedAuthority(it.name) }?.collect(Collectors.toList())
    }
}