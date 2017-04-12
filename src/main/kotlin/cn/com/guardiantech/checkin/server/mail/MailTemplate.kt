package cn.com.guardiantech.checkin.server.mail

import java.util.*

class MailTemplate constructor(var templateContent: String) {
    fun setStringValue(templateKey: String, value: Any) {
        templateContent = this.templateContent.replace("{{" + templateKey + "}}", value.toString());
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