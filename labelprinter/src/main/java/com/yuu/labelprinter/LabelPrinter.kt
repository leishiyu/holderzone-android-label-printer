package com.yuu.labelprinter

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Base64
import android.util.Log
import com.gprinter.command.EscCommand.ENABLE
import com.gprinter.command.GpCom
import com.gprinter.command.LabelCommand
import com.gprinter.command.LabelCommand.FONTTYPE
import com.gprinter.command.LabelCommand.ROTATION
import com.gprinter.service.GpPrintService
import com.yuu.labelprinter.connect.GpConnection
import com.yuu.labelprinter.content.Document
import com.yuu.labelprinter.content.LabelSection
import com.yuu.labelprinter.content.convertable.LabelCoordinateRow
import com.yuu.labelprinter.delegate.GpServiceDelegate
import com.yuu.labelprinter.device.UsbDevicesHelper
import com.yuu.labelprinter.event.ConnectEvent
import com.yuu.labelprinter.event.ConnectEvent.StateNoneEvent
import com.yuu.labelprinter.event.StatusEvent
import com.yuu.labelprinter.log.PrinterLogger
import com.yuu.labelprinter.port.PortParamsHelper
import com.yuu.labelprinter.receiver.GpConnectionReceiver
import com.yuu.labelprinter.receiver.GpResponseReceiver
import com.yuu.labelprinter.receiver.GpStatusReceiver
import com.yuu.labelprinter.registry.GpRegistry
import com.yuu.labelprinter.registry.GpUsbLabelRegistry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter
 * @Description:标签打印机
 * @Version:
 */
class LabelPrinter(private val context: Context) {
    /**
     * 日志工具
     */
    private val printerLogger = PrinterLogger()

    /**
     * 端口参数辅助类
     */
    private val portParamsHelper = PortParamsHelper(context, printerLogger)

    /**
     * GpService静态代理类
     */
    private val gpServiceDelegate = GpServiceDelegate(printerLogger, portParamsHelper)

    /**
     * 打印机连接状态事件流
     */
    private val connectFlow = MutableStateFlow<ConnectEvent>(StateNoneEvent())

    /**
     * 打印机连接Receiver
     */
    private val gpConnectionReceiver = GpConnectionReceiver(connectFlow, portParamsHelper)

    /**
     * 打印机状态响应式观察者
     */
    private val statusFlow = MutableStateFlow<StatusEvent>(StatusEvent.DefaultStatus)

    /**
     * 打印机状态Receiver
     */
    private val gpStatusReceiver = GpStatusReceiver(statusFlow)

    /**
     * 打印机响应Receiver
     */
    private val gpResponseReceiver = GpResponseReceiver()

    /**
     * 打印机ServiceConnection
     */
    private var gpPrinterConnection = GpConnection(portParamsHelper, gpServiceDelegate)

    /**
     * USB设备辅助类
     */
    private val usbDeviceHelper = UsbDevicesHelper(context)

    /**
     * 注册辅助类
     */
    private val gpRegistry: GpRegistry =
        GpUsbLabelRegistry(usbDeviceHelper, portParamsHelper, gpServiceDelegate, connectFlow)



    /**
     * 同步初始化
     */
    fun connect(): Flow<Boolean> {
        //注册usb连接广播
        return registerReceiver()
            .flatMapConcat {
                //绑定服务
                bindService()
            }

    }

    /**
     * 同步打开链接
     */
    fun registerUsb() = registerUsbAsync()

    /**
     * 同步执行打印
     */
    fun print(document: Document, printTimes: Int = 1): LabelPrintResult {
        try {
            printerLogger.d("tainan,执行了print方法")
            printerLogger.d("labelPrint,执行了print方法")
            //首先判断 是否已经注册了USB设备，若没有，则直接抛异常
            // if (!connectSuccess) throw GpHelperException("没有找到标签打印机，请检查连接线后重启程序")
            printerLogger.d("labelPrint,发现lable已经通过了连接")
            val label = LabelCommand()
            val xSize = if (document.page == Document.PAGE_40_30) 40 else 30
            val ySize = if (document.page == Document.PAGE_40_30) 30 else 20
            // 设置标签尺寸,按照实际尺寸设置
            label.addSize(xSize, ySize)
            // 设置标签间隙,按照实际尺寸设置,如果为无间隙纸则设置为 0
            label.addGap(3)
            // 设置打印方向
            label.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)
            // 设置原点坐标
            label.addReference(0, 0)
            // 撕纸模式开启
            label.addTear(ENABLE.ON)
            // 清除打印缓冲区
            label.addCls()
            // 绘制简体中文
            val leftPadding = if (xSize == 40) 10 else 10
            val rightPadding = if (xSize == 40) 10 else 10
            val lineMargin = if (xSize == 40) 32 else 28
            var currentY = if (xSize == 40) 16 else 12
            for (content in document.labelContents) {
                when (content) {
                    is LabelSection -> {
                        if (content.content.startsWith("-")
                            && content.content.endsWith("-")
                        ) {
                            currentY -= lineMargin / 3
                        }
                        label.addText(
                            leftPadding,
                            currentY,
                            FONTTYPE.SIMPLIFIED_CHINESE,
                            ROTATION.ROTATION_0,
                            multipleEnum(content.xMultiple),
                            multipleEnum(content.yMultiple),
                            content.content
                        )
                    }

                    is LabelCoordinateRow -> {
                        val curSize = content.coordinateTextList.size
                        content.coordinateTextList.forEachIndexed { index, it ->
                            var corX = it.xCoordinate
                            if (index == 0) {
                                corX += leftPadding
                            }
                            if (index == curSize - 1) {
                                corX -= rightPadding
                            }
                            label.addText(
                                corX,
                                currentY,
                                FONTTYPE.SIMPLIFIED_CHINESE,
                                ROTATION.ROTATION_0,
                                multipleEnum(1),
                                multipleEnum(1),
                                it.text.text
                            )
                        }
                    }
                }
                currentY += lineMargin
            }
            label.addPrint(1, 1) // 打印标签
            label.addSound(2, 100) // 打印标签后蜂鸣器响
            val data = label.command // 发送数据
            val str = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
            var rel: Int
            var errorText = "打印失败"
            repeat(printTimes) {
                rel = gpServiceDelegate.sendLabelCommand(str)
                val r = GpCom.ERROR_CODE.entries[rel]
                printerLogger.d("labelPrint,命令已生成，开始执行标签打印操作")
                errorText = GpCom.getErrorText(r)
                printerLogger.d("labelPrint,打印指令发送完成")
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    printerLogger.d("labelPrint,标签打印完成，返回失败事件:$errorText")
                    printerLogger.d(errorText)
                    return LabelPrintResult(false, "标签打印机连接异常，请检查连线后重启程序")
                }
            }
            printerLogger.d("labelPrint,标签打印完成，返回成功事件")
            return LabelPrintResult(true, errorText)
        } catch (e: Exception) {
            e.printStackTrace()
            printerLogger.d("labelPrint,标签打印发现错误Exception，返回错误信息：$e")
            return LabelPrintResult(false, "标签打印机连接异常，请检查连线后重启程序")
        } catch (e: Error) {
            e.printStackTrace()
            printerLogger.d("labelPrint,标签打印发现错误Error，返回错误信息：$e")
            return LabelPrintResult(false, "标签打印机连接异常，请检查连线后重启程序")
        } catch (e: Throwable) {
            e.printStackTrace()
            printerLogger.d("labelPrint,标签打印发现错误Throwable，返回错误信息：$e")
            return LabelPrintResult(false, "标签打印机连接异常，请检查连线后重启程序")
        }
    }

    /**
     * 关闭连接
     */
    fun disconnect() {
        printerLogger.i("labelPrint, disconnect所有连接")
        // 解除广播监听
        unregisterReceiver()
        // 解除服务绑定
        unbindService()
        // 断开连接
        gpServiceDelegate.closePort()
    }

    private fun multipleEnum(multiple: Int) = when (multiple) {
        1 -> LabelCommand.FONTMUL.MUL_1
        2 -> LabelCommand.FONTMUL.MUL_2
        3 -> LabelCommand.FONTMUL.MUL_3
        else -> LabelCommand.FONTMUL.MUL_3
    }

    /**
     * 注册广播接收者
     */
    private fun registerReceiver(): Flow<Boolean> {
        return flow<Unit> {
            emit(Unit)
        }.flatMapConcat {
            callbackFlow {
                // 注册连接状态查询广播
                context.registerReceiver(
                    gpConnectionReceiver,
                    IntentFilter(GpCom.ACTION_CONNECT_STATUS)
                )
                // 注册实时状态查询广播
                context.registerReceiver(
                    gpStatusReceiver,
                    IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS)
                )
                // 注册标签打印机响应广播
                context.registerReceiver(
                    gpResponseReceiver,
                    IntentFilter(GpCom.ACTION_LABEL_RESPONSE)
                )
                trySend(true)
                awaitClose {
                    printerLogger.d("注册广播接收器完成")
                }
            }
        }


    }

    /**
     * 广播接收者解除注册
     */
    private fun unregisterReceiver() {
        // 解除注册连接状态查询广播
        context.unregisterReceiver(gpConnectionReceiver)
        // 解除注册实时状态查询广播
        context.unregisterReceiver(gpStatusReceiver)
        // 解除注册标签打印机响应广播
        context.unregisterReceiver(gpResponseReceiver)
    }

    /**
     * 绑定服务
     */
    private fun bindService(): Flow<Boolean> {
        return flow {
            gpPrinterConnection = GpConnection(portParamsHelper, gpServiceDelegate)
            emit(gpPrinterConnection)
        }.flatMapConcat {
            callbackFlow {
                val intent = Intent(context, GpPrintService::class.java)
                val result = context.bindService(intent, it, Context.BIND_AUTO_CREATE)
                trySend(result)
                awaitClose {
                    printerLogger.d("GpPrintService连接成功")
                }
            }
        }

    }


    /**
     * 服务解除绑定
     */
    private fun unbindService() {
        context.unbindService(gpPrinterConnection)
    }

    private fun registerUsbAsync(): Flow<Boolean>{
        return flow {
            emit(Unit)
        }.flatMapConcat {
            gpRegistry.register()
        }.flatMapConcat {
            callbackFlow {
                trySend(it > 0)
                awaitClose{
                    printerLogger.d("注册USB设备完成")
                }
            }
        }
    }

    class LabelPrintResult(val succeed: Boolean, val msg: String)


}