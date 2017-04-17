/**
 * Created by Codetector on 2017/4/17.
 * Project backend
 */
import org.json.JSONArray
import org.junit.Test

import org.junit.Assert.*
class TestJSONArray {
    @Test
    fun testJSONArray(){
        val strinArray = "[9,8,2,43,1,3]"
        val felem = JSONArray(strinArray)[0]
        assertEquals("9", felem.toString())
    }
}