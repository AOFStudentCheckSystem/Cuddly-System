package cn.com.guardiantech.checkin.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Created by Codetector on 2017/4/4.
 * Project backend
 */
@SpringBootApplication
class GuardianCheckServer {
}

fun main(args: Array<String>) {
    SpringApplication.run(GuardianCheckServer::class.java, *args)
}