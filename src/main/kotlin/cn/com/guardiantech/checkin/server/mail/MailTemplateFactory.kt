package cn.com.guardiantech.checkin.server.mail

import com.google.common.io.Resources
import java.nio.charset.Charset

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
class MailTemplateFactory {
    companion object {
        fun createTemlateByFileName(fileName: String): MailTemplate {
            return MailTemplate(Resources.toString(Resources.getResource("/email/tokenNotification.template"), Charsets.UTF_8))
        }
    }
}