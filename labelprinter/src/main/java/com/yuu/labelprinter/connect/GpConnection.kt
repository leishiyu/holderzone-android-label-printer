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
    private val gpServiceDelegate: GpServiceDelegate
) : ServiceConnection {

    private var connectEmitter: MutableStateFlow<ConnectEvent>? = null

    private var disconnectEmitter: MutableStateFlow<ConnectEvent>? = null

    override fun onServiceDisconnected(name: ComponentName?) {
        // aidl service 重置
        gpServiceDelegate.setGpService(null)
        portParamsHelper.setGpService(null)
        // 发送“断开连接”事件
        disconnectEmitter?.value = ConnectEvent.StateConnectingEvent()
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
        // 发送“已连接事件”
        connectEmitter?.value = ConnectEvent.StateConnectingEvent()
    }

    fun setConnectEmitter(connectEmitter:MutableStateFlow<ConnectEvent>) {
        this.connectEmitter = connectEmitter
    }

    fun setDisconnectEmitter(disconnectEmitter: MutableStateFlow<ConnectEvent>) {
        this.disconnectEmitter = disconnectEmitter
    }
}