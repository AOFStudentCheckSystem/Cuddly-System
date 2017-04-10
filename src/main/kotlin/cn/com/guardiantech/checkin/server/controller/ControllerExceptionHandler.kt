package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ExceptionHandler


/**
 * Created by DE_DZ_TBH on 2017/3/28.
 * All rights reserved.
 */

@ControllerAdvice
@CrossOrigin
class ControllerExceptionHandler {
    @ExceptionHandler(Throwable::class)
    fun rootHandler(t: Throwable): ResponseEntity<Throwable> = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t)

    @ExceptionHandler(UnauthorizedException::class)
    fun unAuthorizedExceptionHandler() = ResponseEntity(JSONObject().put("error", "unauthorized").encode(), HttpStatus.UNAUTHORIZED)
}