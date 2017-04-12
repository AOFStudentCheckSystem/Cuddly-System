package cn.com.guardiantech.checkin.server.mail

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Codetector on 2017/4/12.
 * Project backend
 */
class MailTemplateTest {

    @Test
    fun setStringValue() {
        //Create Template
        val t = MailTemplate("a\"{{link}}\"alsdkjflaksd")
        t.setStringValue("link", "test")
        assertEquals("a\"test\"alsdkjflaksd", t.encode())
    }

}