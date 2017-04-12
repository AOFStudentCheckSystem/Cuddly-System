package cn.com.guardiantech.checkin.server.mail

import java.util.*

class MailTemplate constructor(val templateContent: String) {
    fun setStringValue(templateKey: String, value: Any) {
        this.templateContent.replace("{{" + templateKey + "}}", value.toString());
    }

    fun setListValue(templateKey: String, value: List<Any>) {
        var sb: StringBuilder = StringBuilder()
        value.forEach { v ->
            sb.append(v.toString()).append("<br>")
        }
        this.setStringValue(templateKey, sb.toString())
    }

    fun encode() = templateContent
}