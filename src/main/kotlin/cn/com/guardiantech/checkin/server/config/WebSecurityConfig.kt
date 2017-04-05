package cn.com.guardiantech.checkin.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

/**
 * Created by Codetector on 2017/4/5.
 * Project backend
 */
@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter(){
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/**")
    }
}