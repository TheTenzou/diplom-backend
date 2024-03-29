package ru.tenzou.tsodgis.security.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtConfigure(
    private val jwtTokenProvider: JwtTokenProvider,
) :
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(httpSecurity: HttpSecurity) {
        val jwtTokenFilter = JwtTokenFilter(jwtTokenProvider)
        httpSecurity
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}