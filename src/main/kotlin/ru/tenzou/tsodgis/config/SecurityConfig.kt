package ru.tenzou.tsodgis.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import ru.tenzou.tsodgis.security.jwt.JwtAuthenticationEntryPoint
import ru.tenzou.tsodgis.security.jwt.JwtConfigure
import ru.tenzou.tsodgis.security.jwt.JwtTokenProvider

@Configuration
class SecurityConfig @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint
) :
    WebSecurityConfigurerAdapter() {

    companion object {
        private const val ADMIN_ENDPOINT = "/api/v1/admin/**"
        private const val LOGIN_ENDPOINT = "/api/v1/auth/login"
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(LOGIN_ENDPOINT).permitAll()
            .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .apply(JwtConfigure(jwtTokenProvider, jwtAuthenticationEntryPoint))
    }
}