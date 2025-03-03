package com.yuu.labelprinter.device

import android.content.Context
import android.hardware.input.InputManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import java.util.Collections

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.device
 * @Description:Usb设备辅助类
 * @Version:
 */
class UsbDevicesHelper(private val context: Context) {

    fun searchDevices(): List<String>{
        Log.i("deviceManager","labelPrint,执行到了searchUsbDevice")
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        val deviceHashMap = manager.deviceList

        if (deviceHashMap.isEmpty) {
            Log.d("deviceManager","labelPrint,搜索USB设备时未找到Usb设备")
            return Collections.emptyList()
        }
        Log.d("deviceManager","labelPrint,搜索USB设备数量：${deviceHashMap.size}")
        return deviceHashMap.filterValues { checkUsbOrNot(it) }.map { it.key }.toList()
    }
    /**
     * 检查Usb设备是否为Usb打印机
     */
    private fun checkUsbOrNot(usbDevice: UsbDevice): Boolean {
        val pid = usbDevice.productId
        val vid = usbDevice.vendorId
        Log.d("labelPrint","lebelPrint,当前USB设备PID：$pid -- 当前USB设备VID：$vid")
        return vid == 34918 && pid == 256
                || vid == 1137 && pid == 85
                || vid == 6790 && pid == 30084
                || vid == 26728 && pid == 256
                || (vid == 26728 && pid == 512 && usbDevice.manufacturerName!="GPrinter")
                || vid == 26728 && pid == 768
                || vid == 26728 && pid == 1024
                || vid == 26728 && pid == 1280
                || vid == 26728 && pid == 1536
    }
}