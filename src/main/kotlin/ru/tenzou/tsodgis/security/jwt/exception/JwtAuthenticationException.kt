package ru.tenzou.tsodgis.security.jwt.exception

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException : AuthenticationException {
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)
    constructor(msg: String?) : super(msg)
}