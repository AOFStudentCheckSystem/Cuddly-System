package cn.com.guardiantech.checkin.server.httpEntity

import cn.codetector.jet.jetsimplejson.JSONObject

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
}