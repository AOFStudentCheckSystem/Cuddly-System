package cn.com.guardiantech.checkin.server.authentication

import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import cn.com.guardiantech.checkin.server.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by DE_DZ_TBH on 2017/3/29.
 * All rights reserved.
 */
class AuthenticationFilter(private val authenticationService: AuthenticationService): OncePerRequestFilter() {


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse, filterChain: FilterChain) {

        val token = request.getHeader("Authorization")?:""

        try {
            val auth = authenticationService.validateToken(token)
            SecurityContextHolder.getContext().authentication = SessionAuthentication(auth)
        } catch (e: Throwable) {
            response.sendError(401, "Unauthroized")
            return
        }

        filterChain.doFilter(request, response)
    }
}