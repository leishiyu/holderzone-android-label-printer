package com.yuu.holderzone_android_label_printer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.lidroid.xutils.util.LogUtils
import com.yuu.holderzone_android_label_printer.ui.theme.HolderzoneandroidlabelprinterTheme
import com.yuu.labelprinter.LabelPrinter
import com.yuu.labelprinter.content.Document
import com.yuu.labelprinter.content.LabelSection
import com.yuu.labelprinter.content.convertable.LabelCoordinateRow
import com.yuu.labelprinter.content.convertable.LabelFont
import com.yuu.labelprinter.content.convertable.LabelText
import com.yuu.labelprinter.content.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val labelPrinter = LabelPrinter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HolderzoneandroidlabelprinterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Text("连接标签打印服务", modifier = Modifier.clickable{
                                labelPrinter.connect()
                            })
                            Spacer(modifier = Modifier.width(30.dp))
                            Text("注册usb打印设备", modifier = Modifier.clickable{
                                lifecycleScope.launch(Dispatchers.IO) {
                                    labelPrinter.registerUsb().collectLatest {

                                    }
                                }

                            })
                            Spacer(modifier = Modifier.width(30.dp))
                            Text("测试打印", modifier = Modifier.clickable{
                                lifecycleScope.launch(Dispatchers.IO){
                                    labelPrinter.connect()
                                        .flatMapConcat { labelPrinter.registerUsb() }
                                        .map {
                                            labelPrinter.print(getLabelDocument())
                                        }
                                        .collectLatest {
                                            LogUtils.d("打印结果：${it.msg}")
                                        }
                                }
                            })
                        }
                    }

                }
            }
        }
    }

    fun getLabelDocument(page: String = "4030"):Document {
        return document(page) {
            val text = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "（#000000）营养成分表", labelFont = LabelFont.SMALL_BOLD), xPoint = 20, yPoint = 0)
            }
            val line = LabelSection("------------------------")
            val nl = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "能量"), xPoint = 0, yPoint = 0)
                addCoordinateText(text = LabelText(text = "728KJ"), xPoint = 223, yPoint = 0)
            }
            val dbz = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "蛋白质"), xPoint = 0, yPoint = 0)
                addCoordinateText(text = LabelText(text = "5.9g"), xPoint = 223, yPoint = 0)
            }
            val zf = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "脂肪"), xPoint = 0, yPoint = 0)
                addCoordinateText(text = LabelText(text = "5.9g"), xPoint = 223, yPoint = 0)
            }
            val tsh = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "碳水化物化合物"), xPoint = 0, yPoint = 0)
                addCoordinateText(text = LabelText(text = "5.9g"), xPoint = 223, yPoint = 0)
            }
            val text2 = LabelCoordinateRow(defaultAlign = LabelText.Align.Left).apply {
                addCoordinateText(text = LabelText(text = "以上元素含量仅供参考", labelFont = LabelFont.SMALL_BOLD), xPoint = 10, yPoint = 0)
            }
            addLabelContent(text)
            addLabelContent(line)
            addLabelContent(nl)
            addLabelContent(dbz)
            addLabelContent(zf)
            addLabelContent(tsh)
            addLabelContent(text2)

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HolderzoneandroidlabelprinterTheme {
        Greeting("Android")
    }
}