package com.yuu.labelprinter.connect

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.gprinter.aidl.GpService
import com.yuu.labelprinter.delegate.GpServiceDelegate
import com.yuu.labelprinter.event.ConnectEvent
import com.yuu.labelprinter.port.PortParamsHelper
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.connect
 * @Description:GP打印机服务连接
 * @Version:
 */
class GpConnection(
    private val portParamsHelper: PortParamsHelper,
    private val gpServiceDelegate: GpServiceDelegate,
    private val connectFlow:MutableStateFlow<ConnectEvent>
) : ServiceConnection {

    override fun onServiceDisconnected(name: ComponentName?) {
        // aidl service 重置
        gpServiceDelegate.setGpService(null)
        portParamsHelper.setGpService(null)
        connectFlow.value = ConnectEvent.StateNoneEvent()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        // service 代理类
        val gpService = GpService.Stub.asInterface(service)
        // aidl service 赋值
        gpServiceDelegate.setGpService(
            gpService
        )
        portParamsHelper.setGpService(gpService)
        // 加载端口参数，或初始化端口参数
        portParamsHelper.loadOrInitPortParams()
        connectFlow.value = ConnectEvent.StateConnectedEvent()
    }
}