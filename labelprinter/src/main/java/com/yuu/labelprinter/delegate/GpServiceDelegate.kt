package com.yuu.labelprinter.delegate

import com.gprinter.aidl.GpService
import com.gprinter.command.GpCom
import com.gprinter.io.PortParameters
import com.yuu.labelprinter.exception.GpHelperException
import com.yuu.labelprinter.log.PrinterLogger
import com.yuu.labelprinter.port.PortParamsHelper
import kotlin.let

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.delegate
 * @Description:gpService的代理类
 * @Version:
 */
class GpServiceDelegate(
    private val printerLogger: PrinterLogger,
    private val portParamsHelper: PortParamsHelper
) {

    /**
     * AIDL远端服务实例
     */
    private var gpService: GpService? = null

    /**
     * 打印机id
     */
    var printerId: Int = 0
        set(value) {
           portParamsHelper.checkPrinterIdOrThrow(value)
            field = value
        }

    /**
     * 设置AIDL远端服务实例
     */
    fun setGpService(gpService: GpService?) {
        this.gpService = gpService
    }

    /**
     * 打开端口
     *
     * 在此解释一下Gprinter是怎么工作的：
     * Gplink插件可以同时连接3台打印机，依次编号为 0 、1 、2
     * 也就是说，使用者将 usbDevice, BleDevice, EthernetDevice 注册（即openPort操作）到 0, 1, 2 其中一个端口上，并且之后的操作都使用printerId来进行操作
     *
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun openPort() {
        gpService?.let {
            portParamsHelper.checkPortParametersInternal(
                this.printerId
            )
            val errorCodeIndex = when {
                portParamsHelper.isUsb(this.printerId) ->
                    it.openPort(
                        this.printerId,
                        PortParameters.USB,
                        portParamsHelper.getUsbDeviceName(
                            this.printerId
                        ),
                        0
                    )
                portParamsHelper.isEthernet(this.printerId) ->
                    it.openPort(
                        this.printerId,
                        PortParameters.ETHERNET,
                        portParamsHelper.getIpAddr(
                            this.printerId
                        ),
                        portParamsHelper.getPortNumber(
                            this.printerId
                        )
                    )
                portParamsHelper.isBlueTooth(this.printerId) ->
                    it.openPort(
                        this.printerId, PortParameters.BLUETOOTH,
                        portParamsHelper.getBluetoothAddr(
                            this.printerId
                        ), 0
                    )
                else -> 0
            }
            val errorCode = GpCom.ERROR_CODE.entries[errorCodeIndex]
            if (GpCom.ERROR_CODE.SUCCESS != errorCode) {
                if (GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN == errorCode) {
                    portParamsHelper.setPortOpenState(
                        this.printerId,
                        true
                    )
                   printerLogger.d("Printer: $printerId opened already")
                } else {
                    val errorText = GpCom.getErrorText(errorCode)
                    printerLogger.d(errorText)
                }
            } else {
                printerLogger.d("Printer: $printerId opened")
            }
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 关闭端口
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun closePort() {
        gpService?.let {
            it.closePort(this.printerId)
            printerLogger.d("Printer: $printerId closed")
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 获取打印机连接状态
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun getPrinterConnectStatus(): Int {
        gpService?.let {
            return it.getPrinterConnectStatus(this.printerId)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 打印测试页
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun printeTestPage(): Int {
        gpService?.let {
            return it.printeTestPage(this.printerId)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 查询打印机状态
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun queryPrinterStatus(timeout: Int, requestCode: Int) {
        gpService?.let {
            it.queryPrinterStatus(this.printerId, timeout, requestCode)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 查询打印机指令类型
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun getPrinterCommandType(): Int {
        gpService?.let {
            return it.getPrinterCommandType(this.printerId)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 是否是label命令
     */
    fun isLabelCommandType(): Boolean {
        gpService?.let {
            return it.getPrinterCommandType(this.printerId) == GpCom.LABEL_COMMAND
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 是否时esc命令
     */
    fun isEscCommandType(): Boolean {
        gpService?.let {
            return it.getPrinterCommandType(this.printerId) == GpCom.ESC_COMMAND
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 发送 ESC 指令
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun sendEscCommand(base64String: String): Int {
        gpService?.let {
            if (!isEscCommandType()) throw GpHelperException("打印机不是Esc模式")
            return it.sendEscCommand(this.printerId, base64String)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 发送 TSC 指令
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun sendLabelCommand(base64String: String): Int {
        gpService?.let {
            if (!isLabelCommandType()) throw GpHelperException("打印机不是Label模式")
            return it.sendLabelCommand(this.printerId, base64String)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 参与用户体验
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun setUserExperience(userExperience: Boolean) {
        gpService?.isUserExperience(userExperience) ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 获取客户端id
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun getClientID(): String {
        gpService?.let {
            return it.clientID
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 设置服务端ip
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun setServerIP(ip: String, port: Int): Int {
        gpService?.let {
            return it.setServerIP(ip, port)
        } ?: throw GpHelperException("gpService未初始化")
    }

    /**
     * 设置打印机指令类型
     * @see com.gprinter.aidl.GpService (参数定义见 main/aidl/com/gprinter/aidl/GpService.aidl 未编译时的源码)
     */
    fun setCommandType(commandType: Int, response: Boolean) {
        gpService?.setCommandType(this.printerId, commandType, response)
            ?: throw GpHelperException("gpService未初始化")
    }
}