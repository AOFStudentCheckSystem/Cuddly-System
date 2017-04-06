package cn.com.guardiantech.checkin.server.httpEntity

import cn.codetector.jet.jetsimplejson.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Created by Codetector on 2017/4/5.
 * Project backend
 */
class ActionResult(val success: Boolean) {
    private var t: Throwable? = null

    constructor(t: Throwable) : this(false) {
        this.t = t
    }

    override fun toString(): String {
        val returnObject = JSONObject()

        if (!success && t != null) {
            returnObject.put("error", (t as Throwable).message)
        }

        return returnObject.put("success", success).encode()
    }

    fun encode(): ResponseEntity<String> {
        return ResponseEntity(this.toString(), if(success) HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR)
    }
}