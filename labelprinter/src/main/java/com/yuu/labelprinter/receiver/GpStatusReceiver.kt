package com.yuu.labelprinter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gprinter.command.GpCom
import com.yuu.labelprinter.constant.GpConstant
import com.yuu.labelprinter.event.StatusEvent
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.receiver
 * @Description:
 * @Version:
 */
class GpStatusReceiver(
    private var stateFlow: MutableStateFlow<StatusEvent>
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        val requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1)
        val status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16)
        when (requestCode) {
            GpConstant.REQUEST_CODE_QUERY_PRINTER_STATUS -> { // 查询操作 广播
                when (status) {
                    GpCom.STATE_NO_ERR -> {
                        //发送状态事件
                        stateFlow.value = StatusEvent.QueryStatus(true, "打印机正常")
                    }
                    else -> {
                        val errorMessage = when {
                            (status and GpCom.STATE_OFFLINE) > 0 -> "打印机脱机"
                            (status and GpCom.STATE_PAPER_ERR) > 0 -> "打印机缺纸"
                            (status and GpCom.STATE_COVER_OPEN) > 0 -> "打印机开盖"
                            (status and GpCom.STATE_ERR_OCCURS) > 0 -> "打印机出错"
                            (status and GpCom.STATE_TIMES_OUT) > 0 -> "查询超时,请检查打印机是否正常"
                            else -> "未知错误"
                        }
                        stateFlow.value =StatusEvent.QueryStatus(false, errorMessage)
                    }
                }
            }
            GpConstant.REQUEST_CODE_PRINT_RECEIPT -> {  // 打印票据 广播
                val connected = status == GpCom.STATE_NO_ERR
                val statusText = if (status == GpCom.STATE_NO_ERR) "票据打印机连接正常" else "票据打印机连接出错,请检查"
                stateFlow.value = StatusEvent.PrintReceipt(connected, statusText)
            }
            GpConstant.REQUEST_CODE_PRINT_LABEL -> { // 打印标签 广播
                val connected = status == GpCom.STATE_NO_ERR
                val statusText = if (status == GpCom.STATE_NO_ERR) "标签打印机连接正常" else "标签打印机连接出错,请检查"
                stateFlow.value = StatusEvent.PrintLabel(connected, statusText)
            }
        }
    }
}