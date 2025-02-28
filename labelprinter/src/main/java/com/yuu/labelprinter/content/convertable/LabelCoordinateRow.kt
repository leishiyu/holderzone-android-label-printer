package com.yuu.labelprinter.content.convertable

import com.holderzone.library.android.label.printer.content.printable.LabelPrintable

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.content.convertable
 * @Description:指定X坐标、Y坐标的文本行  输出完成后自动换新行
 * @Version:
 */
class LabelCoordinateRow(private val defaultAlign: LabelText.Align = LabelText.Align.Left) : LabelPrintable {

    /**
     * 当前行的所有坐标文本
     */
    val coordinateTextList: MutableList<CoordinateText> = mutableListOf()

    /**
     * 添加指定坐标的字符串
     */
    fun addCoordinateText(text: String, xPoint: Int, yPoint: Int, labelFont: LabelFont = LabelFont.NORMAL) {
        this.addCoordinateText(CoordinateText(LabelText(text, labelFont), xPoint, yPoint, defaultAlign))
    }

    /**
     * 添加指定坐标的文本
     */
    fun addCoordinateText(text: LabelText, xPoint: Int, yPoint: Int) {
        this.addCoordinateText(CoordinateText(text, xPoint, yPoint, defaultAlign))
    }

    /**
     * 添加坐标文本
     */
    fun addCoordinateText(coordinateText: CoordinateText) {
        this.coordinateTextList.add(coordinateText)
    }

    /**
     * 坐标文本
     */
    class CoordinateText(

            /**
             * 打印文本
             */
            val text: LabelText,

            /**
             * X坐标
             */
            val xCoordinate: Int,

            /**
             * Y坐标
             */
            val yCoordinate: Int,

            /**
             * 文本对齐
             */
            val align: LabelText.Align
    )
}