package cn.com.guardiantech.checkin.server.utils

/**
 * Created by Codetector on 2017/4/18.
 * Project backend
 */
fun isValidEmailAddress(email: String): Boolean {
    val ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
    val p = java.util.regex.Pattern.compile(ePattern)
    val m = p.matcher(email)
    return m.matches()
}

fun validateHasNonNumericChar(str: CharSequence): Boolean = !str.matches(Regex("^[0-9]+$"))
fun validateOnlyNumericChar(str: CharSequence): Boolean = str.matches(Regex("^[0-9]+$"))