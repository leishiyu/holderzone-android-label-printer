package com.yuu.labelprinter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gprinter.command.GpCom

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.receiver
 * @Description:
 * @Version:
 */
class GpResponseReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        when(intent.action){
            GpCom.ACTION_RECEIPT_RESPONSE->{

            }
            GpCom.ACTION_LABEL_RESPONSE->{
                val data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE) as ByteArray
                val cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1)
                val d = String(data, 0, cnt)
                if (d[1].toInt() == 0x00) {
                    // do something
                }
            }
        }
    }
}