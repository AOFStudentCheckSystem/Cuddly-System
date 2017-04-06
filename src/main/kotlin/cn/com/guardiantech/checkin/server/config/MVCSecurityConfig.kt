package cn.com.guardiantech.checkin.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Created by Codetector on 2017/4/1.
 * Project classroom_backend
 */
@Configuration
open class MVCSecurityConfig: WebMvcConfigurerAdapter() {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(AuthenticationPrincipalArgumentResolver())
    }
}
