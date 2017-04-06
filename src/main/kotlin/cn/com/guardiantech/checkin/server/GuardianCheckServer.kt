package cn.com.guardiantech.checkin.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@SpringBootApplication
@EnableJpaRepositories
class GuardianCheckServer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GuardianCheckServer::class.java, *args)
        }
    }
}