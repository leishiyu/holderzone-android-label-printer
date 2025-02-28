package com.yuu.labelprinter.event

/**
 * @author terry
 * @date 18-9-30 下午3:30
 */

sealed class ConnectEvent(msg: String) {

    class StateNoneEvent : ConnectEvent("连接断开")

    class StateListenEvent : ConnectEvent("监听中...")

    class StateConnectingEvent : ConnectEvent("连接中...")

    class StateConnectedEvent : ConnectEvent("连接成功")

    class StateInvalidPrinterEvent : ConnectEvent("无效的打印机，请使用GPrinter！")

    class StateValidPrinterEvent : ConnectEvent("有效的GPrinter打印机")
}

sealed class BindServiceEvent(val msg: String) {

    object ServiceConnectedEvent : BindServiceEvent("已连接")

    object ServiceDisconnectedEvent : BindServiceEvent("连接断开")
}

sealed class StatusEvent(val connected: Boolean, val msg: String) {
    //初始状态
    object DefaultStatus : StatusEvent(connected = false, "“打印机初始状态")

    class QueryStatus(connected: Boolean, msg: String) : StatusEvent(connected, msg)

    class PrintReceipt(connected: Boolean, msg: String) : StatusEvent(connected, msg)

    class PrintLabel(connected: Boolean, msg: String) : StatusEvent(connected, msg)
}