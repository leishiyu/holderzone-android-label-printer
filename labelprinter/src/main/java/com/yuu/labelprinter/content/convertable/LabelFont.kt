package com.yuu.labelprinter.content.convertable

import com.yuu.labelprinter.content.printable.LabelConvertable

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.content.convertable
 * @Description:字体样式，作用于打印文本  包括字体大小、字体粗体、字体下划线
 * @Version:
 */
data class LabelFont(

        /**
         * 字体大小
         */
        val size: Size,

        /**
         * 是否粗体
         */
        var isBold: Boolean = false,

        /**
         * 是否下划线
         */
        var isUnderline: Boolean = false

) : LabelConvertable {

    companion object {

        /**
         * 小号字体，无加粗，无下划线
         */
        val SMALL = LabelFont(Size.SMALL, false, false)

        /**
         * 小号字体，加粗，无下划线
         */
        val SMALL_BOLD = LabelFont(Size.SMALL, true, false)

        /**
         * 正常字体，不加粗，无下划线
         */
        val NORMAL = LabelFont(Size.NORMAL, false, false)

        /**
         * 正常字体，加粗，无下划线
         */
        val NORMAL_BOLD = LabelFont(Size.NORMAL, true, false)

        /**
         * 大号字体，不加粗，无下划线
         */
        val BIG = LabelFont(Size.BIG, false, false)

        /**
         * 大号字体，加粗，无下划线
         */
        val BIG_BOLD = LabelFont(Size.BIG, true, false)
    }

    /**
     * 字体大小
     */
    data class Size(val xMultiple: Int, val yMultiple: Int) {

        companion object {

            /**
             * 小号字体
             */
            val SMALL = Size(1, 1)

            /**
             * 中号字体
             */
            val NORMAL = Size(2, 2)

            /**
             * 大号字体
             */
            val BIG = Size(3, 3)
        }
    }
}
