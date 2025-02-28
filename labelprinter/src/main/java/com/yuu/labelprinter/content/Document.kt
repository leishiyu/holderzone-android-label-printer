package com.yuu.labelprinter.content

import com.holderzone.library.android.label.printer.content.printable.LabelPrintable

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.content.printable
 * @Description:DSL生成Document
 * @Version:
 */
data class Document(val page: String = "4030") {


    /**
     * 该打印文档中可打印内容列表
     */
    val labelContents: MutableList<LabelPrintable> = mutableListOf()

    /**
     * 添加一条内容
     *
     * @param content
     * @return
     */
    fun addLabelContent(content: LabelPrintable): Boolean {
        return this.labelContents.add(content)
    }

    /**
     * 添加多条内容，默认跟在原有内容后面
     *
     * @param contents
     * @return
     */
    fun addLabelContents(contents: Collection<LabelPrintable>): Boolean {
        return this.labelContents.addAll(contents)
    }

    /**
     * 在头部插入打印条目
     *
     * @param content
     * @return
     */
    fun addlableFirst(content: LabelPrintable) {
        this.labelContents.add(0, content)
    }


/*    *//**
     * 该打印文档中可打印内容列表
     *//*
    val sections: MutableList<Section> = mutableListOf()


    *//**
     * 添加一条内容
     *
     * @param section
     * @return
     *//*
    fun addSection(section: Section): Boolean {
        return this.sections.add(section)
    }

    *//**
     * 添加多条内容，默认跟在原有内容后面
     *
     * @param sections
     * @return
     *//*
    fun addSections(sections: Collection<Section>): Boolean {
        return this.sections.addAll(sections)
    }

    *//**
     * 在头部插入打印条目
     *
     * @param section
     * @return
     *//*
    fun addFirst(section: Section) {
        this.sections.add(0, section)
    }*/

    companion object {

        const val PAGE_40_30 = "4030"

        const val PAGE_30_20 = "3020"
    }
}

data class LabelSection(
    val content: String,
    val xMultiple: Int = 1,
    val yMultiple: Int = 1
):LabelPrintable


inline fun document(page: String = "4030", initializer: Document.() -> Unit): Document {
    return Document(page).apply { initializer() }
}

private fun Document.LabelSection(string: String) {}
