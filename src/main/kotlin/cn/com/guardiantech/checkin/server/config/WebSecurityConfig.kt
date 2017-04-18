package cn.com.guardiantech.checkin.server.config

import cn.com.guardiantech.checkin.server.authentication.AuthenticationFilter
import cn.com.guardiantech.checkin.server.authentication.Permission
import cn.com.guardiantech.checkin.server.service.AuthenticationService
import cn.com.guardiantech.checkin.server.service.UserRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder



/**
 * Created by Codetector on 2017/4/5.
 * Project backend
 */
@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter(){

    @Autowired
    lateinit var authenticationDetail : UserRegistrationService

    @Autowired
    lateinit var authenticationService: AuthenticationService

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")
        web.ignoring()
                .antMatchers("/auth/auth")
                .antMatchers("/auth/login")
                .antMatchers("/auth/register")
                .antMatchers("/auth/verify-token**")
                //Event
                .antMatchers("/event/list")
                .antMatchers("/event/listall")
                .antMatchers("/event/list/**")
                .antMatchers("/event/group/list")
                .antMatchers("/event/group/listall")
                .antMatchers("/event/group/list-available")
                .antMatchers("/event/group/list/**")
                //Signup
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.antMatcher("/**")
                .addFilterBefore(AuthenticationFilter(authenticationService), BasicAuthenticationFilter::class.java)
        http.authorizeRequests()
                //Auth
                .antMatchers("/auth/admin/**").hasRole(Permission.ADMIN.stringValue)
                //CheckIn
                .antMatchers("/checkin/**").hasRole(Permission.TABLET.stringValue)
                // Events
                .antMatchers("/event/create").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/event/records/list").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/event/remove/**").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/event/group/new").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/event/group/edit/**").hasRole(Permission.TABLET.stringValue)
                // Sign ups
                .antMatchers("/signup/create").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/signup/edit/**").hasRole(Permission.TABLET.stringValue)
                .antMatchers("/signup/find/*").hasRole(Permission.SIGNUP.stringValue)
                .antMatchers("/signup/list").hasRole(Permission.SIGNUP.stringValue)
                .antMatchers("/signup/list/**").hasRole(Permission.SIGNUP.stringValue)
                .antMatchers("/signup/signup").hasRole(Permission.SIGNUP.stringValue)
                .antMatchers("/signup/signup/*").hasRole(Permission.SIGNUP.stringValue)
                // Student
                .antMatchers("/student/list").hasRole(Permission.USER.stringValue)
                .antMatchers("/student/list/**").hasRole(Permission.USER.stringValue)
                .antMatchers("/student/listall").hasRole(Permission.USER.stringValue)
                .antMatchers("/student/create").hasRole(Permission.ADMIN.stringValue)
                .antMatchers("/student/new").hasRole(Permission.ADMIN.stringValue)
                .antMatchers("/student/edit/**").hasRole(Permission.TABLET.stringValue)
                .anyRequest().hasRole(Permission.ADMIN.stringValue)
    }
}