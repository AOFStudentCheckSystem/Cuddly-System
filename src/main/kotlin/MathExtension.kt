/**
 * Created by Codetector on 2017/4/14.
 * Project backend
 */
fun Long.abs(): Long {
    return Math.abs(this)
}

fun Int.unitDirection(): Int {
    return if (this > 0) 1 else if (this < 0) -1 else 0
}

fun Long.unitDirection(): Long {
    return if (this > 0) 1L else if (this < 0) -1L else 0L
}