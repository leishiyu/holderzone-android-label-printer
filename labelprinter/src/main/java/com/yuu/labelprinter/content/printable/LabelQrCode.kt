package com.yuu.labelprinter.content.printable

import com.holderzone.library.android.label.printer.content.printable.LabelPrintable

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.log
 * @Description:二维码 输出完成后自动换新行
 * @Version:
 */
data class LabelQrCode(

        /**
         * 内容
         */
        val content: String,

        /**
         * 宽度(像素)
         */
        val width: Int = 0,

        /**
         * 上下文间距(像素)
         */
        val margin: Int = 0

) : LabelPrintable
