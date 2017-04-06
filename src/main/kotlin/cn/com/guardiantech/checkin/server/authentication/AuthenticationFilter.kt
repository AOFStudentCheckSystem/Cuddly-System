package cn.com.guardiantech.checkin.server.authentication

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
class AuthenticationFilter: OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse, filterChain: FilterChain) {

        val token = request.getHeader("Authorization")?:""

        if (token.isNullOrEmpty()){
            throw IllegalStateException("No token")
        }
//        val auth = accountService.verifyToken(token)
//        SecurityContextHolder.getContext().authentication = SessionAuthentication(auth, accountService)

        filterChain.doFilter(request, response)
    }
}