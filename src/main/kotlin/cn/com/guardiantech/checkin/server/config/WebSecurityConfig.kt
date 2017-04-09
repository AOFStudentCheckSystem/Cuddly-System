package cn.com.guardiantech.checkin.server.config

import cn.com.guardiantech.checkin.server.authentication.AuthenticationFilter
import cn.com.guardiantech.checkin.server.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Created by Codetector on 2017/4/5.
 * Project backend
 */
@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter(){

    @Autowired
    lateinit var authenticationService: AuthenticationService

    override fun configure(web: WebSecurity) {
        web.ignoring()
                .antMatchers("/auth/auth")
                .antMatchers("/auth/login")
                .antMatchers("/auth/register")
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.antMatcher("/**")
                .addFilterBefore(AuthenticationFilter(authenticationService), BasicAuthenticationFilter::class.java)
    }
}