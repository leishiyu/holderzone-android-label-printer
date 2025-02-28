package com.yuu.labelprinter.registry

import android.util.Log
import com.gprinter.io.PortParameters
import com.yuu.labelprinter.delegate.GpServiceDelegate
import com.yuu.labelprinter.device.UsbDevicesHelper
import com.yuu.labelprinter.event.ConnectEvent
import com.yuu.labelprinter.port.PortParamsHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.registry
 * @Description:佳博usb标签打印机注册器
 * @Version:
 */
class GpUsbLabelRegistry(
    private val usbDevicesHelper: UsbDevicesHelper,
    private val portParamsHelper: PortParamsHelper,
    private val gpServiceDelegate: GpServiceDelegate,
    private val connectFlow: MutableStateFlow<ConnectEvent>
) : GpRegistry {
    override fun register(): Flow<Int> {
        return flow {
            Log.d("GpUsbLabelRegistry", "注册设备到GpLink")
            val usbDevices = usbDevicesHelper.searchDevices()
            var printerId = 0
            var usbDeviceId = 0
            if (usbDevices.isEmpty()) {
                emit(-1) // 发射一个错误码表示未找到打印机
                return@flow
            }
            Log.d("GpUsbLabelRegistry", "lebelPrint,打印机列表有数据")
            val maxSize = max(usbDevices.size, PortParamsHelper.MAX_PORT_SIZE)
            val mutex = Mutex()
            val usbDevicesFlow = flow {
                for (i in 0 until usbDevices.size) {
                    emit(i)
                }
            }
            val connectEventFlow = connectFlow.filter { isEndConnectEvent(it) }
                .onEach {
                    Log.d(
                        "GpUsbLabelRegistry",
                        "lebelPrint,开始执行过滤广播操作，当前线程，${Thread.currentThread().name}"
                    )
                }

            usbDevicesFlow.onEach {
                Log.d(
                    "GpUsbLabelRegistry",
                    "lebelPrint,开始执行openPort操作，当前线程，${Thread.currentThread().name}"
                )
                openPort(printerId, usbDevices[usbDeviceId])
            }.zip(connectEventFlow) { _, connectEvent ->
                connectEvent
            }
                .map { connectEvent ->
                    mutex.withLock {
                        when (connectEvent) {
                            is ConnectEvent.StateValidPrinterEvent -> {
                                if (gpServiceDelegate.isLabelCommandType()) {
                                    printerId++
                                } else {
                                    gpServiceDelegate.closePort()
                                }
                            }

                            is ConnectEvent.StateInvalidPrinterEvent -> {
                                gpServiceDelegate.closePort()
                            }

                            else -> Unit
                        }
                        usbDeviceId++
                        if (printerId < PortParamsHelper.MAX_PORT_SIZE && usbDeviceId < usbDevices.size) {
                            printerId
                        } else {
                            -1
                        }
                    }
                }
                .distinctUntilChanged()
                .collect { result ->
                    if (result == -1) {
                        emit(-1) // 发射一个错误码表示未找到打印机
                    } else {
                        emit(result - 1)
                    }
                }

            if (printerId == 0) {
                emit(-1) // 发射一个错误码表示未找到打印机
            }
        }
    }

    private fun openPort(printerId: Int, deviceName: String) {
        portParamsHelper.setPortType(printerId, PortParameters.USB)
        portParamsHelper.setUsbDeviceName(printerId, deviceName)
        gpServiceDelegate.printerId = printerId
        gpServiceDelegate.openPort()
    }

    private fun isEndConnectEvent(event: ConnectEvent): Boolean {
        return event is ConnectEvent.StateValidPrinterEvent || event is ConnectEvent.StateInvalidPrinterEvent
    }
}