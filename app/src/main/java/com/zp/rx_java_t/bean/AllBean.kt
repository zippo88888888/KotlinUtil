package com.zp.rx_java_t.bean

import java.io.Serializable

/**
 * 本地文件 实体类
 */
data class LocalFileBean(
        var name: String = "",
        var path: String = "",
        var date: Long = 0L,    // 时间
        var type: String = "", // 文件类型
        var size: Double = 0.0
) : Serializable