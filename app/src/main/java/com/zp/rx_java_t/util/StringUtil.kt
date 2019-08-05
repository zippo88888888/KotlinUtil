package com.zp.rx_java_t.util

import com.zp.rx_java_t.content.IS_OFFICIAL
import com.zp.rx_java_t.content.doubleFro2
import com.zp.rx_java_t.content.isNull
import java.util.regex.Pattern

object StringUtil {

    private const val STRING_REGEX = "^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|,:()\"\'\n，。！、：（）“”《》？]+$"

    /**
     * 验证手机号是否正确
     */
    fun checkPhone(phone: String): Boolean {
        val p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$") // 验证手机号
        val m = p.matcher(phone)
        return m.matches()
    }

    /**
     * 判断输入的文本是否 是数字,英文字母和中文
     * @return false 可能包含表情
     */
    fun checkTxtTrue(s: String): Boolean {
        if (s.isNull()) {
            return true
        }
        val p = Pattern.compile(STRING_REGEX)
        val m = p.matcher(s.replace(" ".toRegex(), ""))
        return m.matches()
    }

    /**
     * 金额格式化
     * @param money             金额：单位分
     * @param needDoubleFro2    true--保留两位小数，false--自动判断是否需要显示后两位小数
     */
    fun priceFormat(money: Any, needDoubleFro2: Boolean = true): String {
        val price = when (money) {
            is Long -> money
            is String -> if (money.isEmpty()) 0L else money.toLong()
            else -> 0L
        }
        if (price <= 0L) return "0.00"
        val str = doubleFro2(price / 100.00)
        if (needDoubleFro2) {
            return str
        }
        val last2Value = str.substring(str.indexOf(".") + 1)
        return try {
            val number = Integer.parseInt(last2Value)
            if (number <= 0) {
                (price / 100).toString()
            } else {
                str
            }
        } catch (e: Exception) {
            if (!IS_OFFICIAL) e.printStackTrace()
            str
        }
    }


}