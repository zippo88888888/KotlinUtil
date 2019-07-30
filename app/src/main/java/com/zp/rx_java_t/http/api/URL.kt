package com.zp.rx_java_t.http.api

object URL {

    private const val TEST_URL = "http://192.168.0.106:6080/"
    private const val REAL_URL = ""

    private const val URL_INFIX = "zxzx/app/"

    const val ROOT_URL = TEST_URL + URL_INFIX

    const val LOGIN = "user/login"
    const val GET_USER_INFO = "user/userInfo"

    fun getNeedUrl(url: String) = URL_INFIX + url


}