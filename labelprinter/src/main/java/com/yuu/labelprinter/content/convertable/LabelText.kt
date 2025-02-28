package com.yuu.labelprinter.content.convertable

import com.yuu.labelprinter.content.printable.LabelConvertable

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.content.convertable
 * @Description:打印文本  包括文本字符串，文本字体
 * @Version:
 */
data class LabelText(

        /**
         * 文本字符串
         */
        val text: String,

        /**
         * 文本字体
         */
        val labelFont: LabelFont = LabelFont.Companion.SMALL

) : LabelConvertable {

    /**
     * 打印文本对齐方式
     */
    enum class Align {

        /**
         * 左对齐
         */
        Left,

        /**
         * 居中对齐
         */
        Center,

        /**
         * 右对齐
         */
        Right
    }

    companion object {

        val EMPTY = LabelText("")

        val BLANK = LabelText(" ")
    }
}
