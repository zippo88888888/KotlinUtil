package com.zp.rx_java_t.content

object Content {


    const val SP_FILE_NAME = "zp_rx_java_t"
    const val LOG_TAG = "APP_LOG"


    /** 刷新或正常状态  */
    const val REFRESH = 0x1
    /** 加载更多状态  */
    const val LOAD_MORE = 0x2

    /** 请求成功的状态码  */
    const val REQUEST_SUCCESS_CODE = 0
    /** 登录时间已过期  */
    const val REQUEST_LOGIN_TIME_FAILED_CODE = 401
    /** 服务器内部错误  */
    const val REQUEST_SERVICE_ERROR_CODE = 500
    /** 当前账号在其他设备登录  */
    const val REQUEST_OTHER_LOGIN_CODE = 501


    // 文件类型
    const val TXT = "txt"
    const val DOC = "doc"
    const val DOCX = "docx"
    const val XLS = "xls"
    const val XLSX = "xlsx"
    const val PPT = "ppt"
    const val PPTX = "pptx"
    const val PDF = "pdf"
}