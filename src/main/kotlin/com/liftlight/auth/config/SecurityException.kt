package com.liftlight.auth.config

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.AuthenticationException


class AuthMethodNotSupportedException(msg: String?) : AuthenticationServiceException(msg)

class JwtExpiredTokenException(msg: String?, t: Throwable?) : AuthenticationException(msg, t)