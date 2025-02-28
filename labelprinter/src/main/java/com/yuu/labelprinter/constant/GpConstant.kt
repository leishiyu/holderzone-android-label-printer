package com.yuu.labelprinter.constant

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.constant
 * @Description:标签打印常量
 * @Version:
 */
object GpConstant {

    /**
     * 打印票据 请求码
     */
    const val REQUEST_CODE_PRINT_RECEIPT = 0xfc

    /**
     * 打印标签 请求码
     */
    const val REQUEST_CODE_PRINT_LABEL = 0xfd

    /**
     * 查询打印机状态 请求码
     */
    const val REQUEST_CODE_QUERY_PRINTER_STATUS = 0xfe

    /**
     * 超时时间 millis
     * USB一般200-300即可
     * BLE一般2000-3000即可
     */
    const val TIME_OUT_MILLIS = 200
}