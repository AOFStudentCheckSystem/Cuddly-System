package cn.com.guardiantech.checkin.server.mail

import com.google.common.io.Resources
import java.io.File
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
class MailTemplateFactory {
    companion object {
        fun createTemplateByFileName(fileName: String): MailTemplate {
            println(Arrays.toString(File(this.javaClass.getResource("/email").path).list()))
            return MailTemplate(Resources.toString(this.javaClass.getResource("/email/$fileName.template"), Charsets.UTF_8))
        }
    }
}