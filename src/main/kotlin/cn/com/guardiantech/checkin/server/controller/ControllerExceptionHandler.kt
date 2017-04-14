package cn.com.guardiantech.checkin.server.controller

import cn.codetector.jet.jetsimplejson.JSONObject
import cn.codetector.jet.jetsimplejson.exception.DecodeException
import cn.com.guardiantech.checkin.server.exception.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.NoSuchElementException
import kotlin.IllegalArgumentException


/**
 * Created by DE_DZ_TBH on 2017/3/28.
 * All rights reserved.
 */

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(UnauthorizedException::class)
    fun unAuthorizedExceptionHandler() = ResponseEntity(JSONObject().put("error", "unauthorized").encode(), HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(DecodeException::class)
    fun decodeExceptionHandler() = ResponseEntity(JSONObject().put("error", "unable to decode json").encode(), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingParamException(e: MissingServletRequestParameterException) = ResponseEntity(JSONObject().put("error", "missingParam").put("name", e.parameterName).put("type", e.parameterType).encode(), HttpStatus.NOT_ACCEPTABLE)

    @ExceptionHandler(NoSuchElementException::class)
    fun elementNotFound(e: NoSuchElementException): ResponseEntity<String> = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentExceptionHandler(e: IllegalArgumentException): ResponseEntity<String> = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message?:"")

    @ExceptionHandler(Throwable::class)
    fun rootHandler(t: Throwable): ResponseEntity<String> = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.message)
}