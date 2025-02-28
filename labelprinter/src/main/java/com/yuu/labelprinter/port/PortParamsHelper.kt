package com.yuu.labelprinter.port

import android.content.Context
import android.os.RemoteException
import com.gprinter.aidl.GpService
import com.gprinter.io.GpDevice
import com.gprinter.io.PortParameters
import com.gprinter.save.PortParamDataBase
import com.gprinter.service.GpPrintService
import com.yuu.labelprinter.exception.GpHelperException
import com.yuu.labelprinter.log.PrinterLogger

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.port
 * @Description:
 * @Version:
 */
class PortParamsHelper(
    private val context: Context,
    private val printerLogger: PrinterLogger
) {

    /**
     * 接口参数
     */
    private val portParams: Array<PortParameters> = Array(MAX_PORT_SIZE) { PortParameters() }

    /**
     * AIDL远端服务实例
     */
    private var gpService: GpService? = null

    /**
     * 设置AIDL远端服务实例
     */
    fun setGpService(gpService: GpService?) {
        this.gpService = gpService
    }

    /**
     * 初始化(装载)端口参数
     */
    fun loadOrInitPortParams() {
        // 获取端口连接状态
        val connectStatus = BooleanArray(MAX_PORT_SIZE) { false }
        try {
            for (i in 0 until MAX_PORT_SIZE) {
                connectStatus[i] = GpDevice.STATE_CONNECTED == gpService?.getPrinterConnectStatus(i)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
            printerLogger.d(e.message ?: e.toString())
        }
        // 获取端口参数
        val portParamDataBase = PortParamDataBase(context)
        for (i in 0 until MAX_PORT_SIZE) {
            val portParameter = portParamDataBase.queryPortParamDataBase(i.toString())
                ?: portParams[i]
            portParameter.portOpenState = connectStatus[i]
            portParams[i] = portParameter
        }
        portParamDataBase.close()
    }

    /**
     * 设置该设备端口类型
     *
     * 0：SERIAL
     * 1：PARALLEL
     * 2：USB
     * 3：ETHERNET
     * 4：BLUETOOTH
     * 5：UNDEFINE
     *
     * @param printerId 打印机id
     * @param portType 端口类型
     */
    fun setPortType(printerId: Int, portType: Int) {
        checkPrinterIdOrThrow(printerId)
        portParams[printerId].portType = portType
    }

    /**
     * 获取该设备端口类型
     *
     * @param printerId 打印机id
     * @return 该设备端口类型
     */
    fun getPortType(printerId: Int): Int {
        checkPrinterIdOrThrow(printerId)
        return portParams[printerId].portType
    }

    /**
     * 该设备是否为Usb设备
     *
     * @param printerId 打印机id
     */
    fun isUsb(printerId: Int): Boolean {
        checkPrinterIdOrThrow(printerId)
        return portParams[printerId].portType == PortParameters.USB
    }

    /**
     * 该设备是否为网络设备
     *
     * @param printerId 打印机id
     */
    fun isEthernet(printerId: Int): Boolean {
        checkPrinterIdOrThrow(printerId)
        return portParams[printerId].portType == PortParameters.ETHERNET
    }

    /**
     * 该设备是否为蓝牙设备
     *
     * @param printerId 打印机id
     */
    fun isBlueTooth(printerId: Int): Boolean {
        checkPrinterIdOrThrow(printerId)
        return portParams[printerId].portType == PortParameters.BLUETOOTH
    }

    /**
     * 该设备为蓝牙设备时：设置设备address
     *
     * @param printerId 打印机id
     * @param bluetoothAddr 蓝牙设备地址
     */
    fun setBluetoothAddr(printerId: Int, bluetoothAddr: String) {
        checkPrinterIdOrThrow(printerId)
        if (!isBlueTooth(printerId)) {
            throw GpHelperException("该设备不是蓝牙设备")
        }
        portParams[printerId].bluetoothAddr = bluetoothAddr
    }

    /**
     * 该设备为蓝牙设备时：获取设备address
     *
     * @param printerId 打印机id
     */
    fun getBluetoothAddr(printerId: Int): String {
        checkPrinterIdOrThrow(printerId)
        if (!isBlueTooth(printerId)) {
            throw GpHelperException("该设备不是蓝牙设备")
        }
        return portParams[printerId].let {
            checkPortParameters(it)
            it.bluetoothAddr
        }
    }

    /**
     * 该设备为USB设备时：设置设备name
     *
     * @param printerId 打印机id
     * @param usbDeviceName Usb设备名
     */
    fun setUsbDeviceName(printerId: Int, usbDeviceName: String) {
        checkPrinterIdOrThrow(printerId)
        if (!isUsb(printerId)) {
            throw GpHelperException("该设备不是USB设备")
        }
        portParams[printerId].usbDeviceName = usbDeviceName
    }

    /**
     * 该设备为USB设备时：获取设备name
     *
     * @param printerId 打印机id
     */
    fun getUsbDeviceName(printerId: Int): String {
        checkPrinterIdOrThrow(printerId)
        if (!isUsb(printerId)) {
            throw GpHelperException("该设备不是USB设备")
        }
        return portParams[printerId].let {
            checkPortParameters(it)
            it.usbDeviceName
        }
    }

    /**
     * 该设备为网络设备时：设置设备ip
     *
     * @param printerId 打印机id
     * @param ipAddr ip地址
     */
    fun setIpAddr(printerId: Int, ipAddr: String) {
        checkPrinterIdOrThrow(printerId)
        if (!isEthernet(printerId)) {
            throw GpHelperException("该设备不是网络设备")
        }
        portParams[printerId].ipAddr = ipAddr
    }

    /**
     * 该设备为网络设备时：获取设备ip
     */
    fun getIpAddr(printerId: Int): String {
        checkPrinterIdOrThrow(printerId)
        if (!isEthernet(printerId)) {
            throw GpHelperException("该设备不是网络设备")
        }
        return portParams[printerId].let {
            checkPortParameters(it)
            it.ipAddr
        }
    }

    /**
     * 该设备为网络设备时：设置设备port
     *
     * @param printerId 打印机id
     * @param portNumber 端口号
     */
    fun setPortNumber(printerId: Int, portNumber: Int) {
        checkPrinterIdOrThrow(printerId)
        if (!isEthernet(printerId)) {
            throw GpHelperException("该设备不是网络设备")
        }
        portParams[printerId].portNumber = portNumber
    }

    /**
     * 该设备为网络设备时：获取设备port
     */
    fun getPortNumber(printerId: Int): Int {
        checkPrinterIdOrThrow(printerId)
        if (!isEthernet(printerId)) {
            throw GpHelperException("该设备不是网络设备")
        }
        return portParams[printerId].let {
            checkPortParameters(it)
            it.portNumber
        }
    }

    /**
     * 设置该设备的端口开启状态
     *
     * @param printerId 打印机id
     * @param portOpenState 端口开启状态
     */
    fun setPortOpenState(printerId: Int, portOpenState: Boolean) {
        checkPrinterIdOrThrow(printerId)
        portParams[printerId].portOpenState = portOpenState
    }

    /**
     * 获取该设备的端口开启状态
     */
    fun getPortOpenState(printerId: Int): Boolean {
        checkPrinterIdOrThrow(printerId)
        return portParams[printerId].portOpenState
    }

    /**
     * 检查printerId有效性
     */
    fun checkPrinterIdOrThrow(printerId: Int) {
        if (printerId !in 0 until MAX_PORT_SIZE) {
            throw GpHelperException("无效端口")
        }
    }

    /**
     * 获取端口参数信息
     */
    fun getPortParametersInfo(portParameters: PortParameters): String = with(portParameters) {
        checkPortParameters(this)
        when (portParameters.portType) {
            PortParameters.BLUETOOTH -> "蓝牙设备，MAC地址：$bluetoothAddr"
            PortParameters.ETHERNET -> "网络设备，IP地址：$ipAddr $portNumber"
            PortParameters.USB -> "USB设备，设备名：$usbDeviceName"
            else -> "未知设备"
        }
    }

    /**
     * 检查端口参数是否合法
     */
    fun checkPortParameters(portParameters: PortParameters) = with(portParameters) {
        when (portParameters.portType) {
            PortParameters.BLUETOOTH -> {
                if (bluetoothAddr.isBlank()) throw GpHelperException("无效的蓝牙地址")
            }
            PortParameters.ETHERNET -> {
                if (ipAddr.isBlank()) throw GpHelperException("无效的ip地址")
                if (portNumber <= 0) throw GpHelperException("无效的端口号")
            }
            PortParameters.USB -> {
                if (usbDeviceName.isBlank()) throw GpHelperException("无效的usb设备名")
            }
            else -> throw GpHelperException("无效的端口")
        }
    }

    /**
     * 检查端口参数是否合法
     */
    fun checkPortParametersInternal(printerId: Int) {
        checkPrinterIdOrThrow(printerId)
        checkPortParameters(portParams[printerId])
    }

    companion object {

        /**
         * 支持的最大端口连接数
         */
        const val MAX_PORT_SIZE: Int = GpPrintService.MAX_PRINTER_CNT
    }
}