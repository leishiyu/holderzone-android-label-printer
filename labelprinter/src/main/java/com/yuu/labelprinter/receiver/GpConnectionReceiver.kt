package com.yuu.labelprinter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gprinter.io.GpDevice
import com.gprinter.service.GpPrintService
import com.yuu.labelprinter.event.ConnectEvent
import com.yuu.labelprinter.port.PortParamsHelper
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.receiver
 * @Description:佳博标签打印机连接状态接收者
 * @Version:0.0.1
 */
class GpConnectionReceiver(
    private val connectFlow: MutableStateFlow<ConnectEvent>,
    private val portParamsHelper: PortParamsHelper
) : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?: return
        val connectStatus = intent.getIntExtra(GpPrintService.CONNECT_STATUS,0)
        val printerId = intent.getIntExtra(GpPrintService.PRINTER_ID,0)
        when(connectStatus){
            GpDevice.STATE_NONE -> {
                portParamsHelper.setPortOpenState(printerId, false)
                connectFlow.value = ConnectEvent.StateNoneEvent()
            }
            GpDevice.STATE_LISTEN -> {
                portParamsHelper.setPortOpenState(printerId, false)
                connectFlow.value = ConnectEvent.StateListenEvent()
            }
            GpDevice.STATE_CONNECTING -> {
                portParamsHelper.setPortOpenState(printerId, false)
                connectFlow.value = ConnectEvent.StateConnectingEvent()
            }
            GpDevice.STATE_CONNECTED -> {
                portParamsHelper.setPortOpenState(printerId, false)
                connectFlow.value = ConnectEvent.StateConnectedEvent()
            }
            GpDevice.STATE_VALID_PRINTER -> {
                portParamsHelper.setPortOpenState(printerId, true)
                connectFlow.value = ConnectEvent.StateValidPrinterEvent()
            }
            GpDevice.STATE_INVALID_PRINTER -> {
                portParamsHelper.setPortOpenState(printerId, false)
                connectFlow.value = ConnectEvent.StateInvalidPrinterEvent()
            }
        }
    }
}