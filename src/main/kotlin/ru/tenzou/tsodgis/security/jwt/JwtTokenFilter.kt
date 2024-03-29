package ru.tenzou.tsodgis.security.jwt

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtTokenFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {


    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = jwtTokenProvider.resolveToken(request as HttpServletRequest)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(token)
        }

        chain?.doFilter(request, response)
    }
}