package com.yuu.labelprinter.content

import com.yuu.labelprinter.content.convertable.LabelCoordinateRow
import com.holderzone.library.android.label.printer.content.printable.LabelPrintable
import com.yuu.labelprinter.content.convertable.LabelText

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.content.printable
 * @Description:DSL生成Document
 * @Version:
 */
inline fun document(initializer: Document.() -> Unit): Document {
    return Document().apply { initializer() }
}

inline fun printables(initializer: MutableList<LabelPrintable>.() -> Unit): List<LabelPrintable> {
    return mutableListOf<LabelPrintable>().apply { initializer() }
}


/*inline fun section(text: String, defaultFont: LabelFont = LabelFont.NORMAL,
                   align: LabelText.Align = LabelText.Align.Left,
                   initializer: LabelSection.() -> Unit): LabelSection {
    return LabelSection(text, defaultFont, align).apply { initializer() }
}*/


/**
 * DSL生成CoordinateRow
 */
inline fun labelCoordinateRow(align: LabelText.Align, initializer: LabelCoordinateRow.() -> Unit): LabelCoordinateRow {
    return LabelCoordinateRow(align).apply { initializer() }
}
