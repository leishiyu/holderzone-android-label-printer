package com.yuu.labelprinter.log

import android.util.Log

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.log
 * @Description:
 * @Version:
 */
class PrinterLogger {
    fun d(msg: String) {
        Log.d("LabelPrinter","[DEBUG] $msg")
    }

    fun i(msg: String) {
        Log.i("LabelPrinter","[INFO] $msg")
    }

    fun w(msg: String) {
        Log.w("LabelPrinter","[WARN] $msg")
    }

    fun e(msg: String) {
        Log.e("LabelPrinter","[ERROR] $msg")
    }
}