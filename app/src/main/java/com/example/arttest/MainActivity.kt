package com.example.arttest

import android.app.Application
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mylib.Notification
import com.example.mylib.net.Mqtt
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.startActivity
import android.content.Context
import android.graphics.Bitmap

//const val SUB_TOPIC = "arttest/#"
//
//const val SERVER_URI = "tcp://172.30.1.1:1883"



class App : Application() {
    companion object {
        var bitmap: Bitmap? =null
    }
    override fun onCreate() {
        super.onCreate()

    }
}



class MainActivity : AppCompatActivity() {

    lateinit var mqttClient: Mqtt



    companion object {
        const val CHANNEL_ID1 = "com.example.arttest"  // noti 할때의 포트번호라 생각하자
//        const val CHANNEL_ID2 = "com.example.lockstop2"
        const val CHANNEL_NAME = "My Channel"
        const val CHANNEL_DESCRIPTION = "Channel Test"
        const val NOTIFICATION_REQUEST = 0
        const val NOTIFICATION_ID1 = 100
//        const val NOTIFICATION_ID2 = 200



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




//        mqttClient = Mqtt(this, SERVER_URI)
//        try {
//            mqttClient.setCallback(::onReceived)
//            Log.d("Mqtt", "connect")
//            mqttClient.connect(arrayOf<String>(SUB_TOPIC))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        btn_start.setOnClickListener {
            startActivity<PictureActivity>(
            )
        }

    }






    fun onReceived(topic: String, message: MqttMessage) {   // 메시지로만 받는 행위이기때문에 수정 필요함
        var msg = String(message.payload)

//        Log.i("mqtt_main", "$topic: $msg")

//        when (topic) {
//            "arttest/#" -> {
//                val noti = Notification(this)
//                noti.createNotificationChannel(CHANNEL_ID1, CHANNEL_NAME, CHANNEL_DESCRIPTION)
//                val pendingIntent = noti.getPendingIntent(
//                    DoorLockActivity::class.java,
//                    NOTIFICATION_REQUEST,
//                    msg)
//                val open = BitmapFactory.decodeResource(resources,R.drawable.open_noti)
//                val error = BitmapFactory.decodeResource(resources,R.drawable.error3_noti)
//
//
//                when (msg) {
//                    "open" -> noti.notifyBasic(CHANNEL_ID1, NOTIFICATION_ID1,
//                        "Alarm", "문열림",
//                        R.drawable.lockstop_noti,open, pendingIntent)
//                    "error3" -> noti.notifyBasic(CHANNEL_ID1, NOTIFICATION_ID1,
//                        "Alarm", "비밀번호 3회 오류",
//                        R.drawable.lockstop_noti,error ,pendingIntent)
//
//                }
//            }
//            "iot/CJ" -> {
//                val noti = Notification(this)
//                noti.createNotificationChannel(CHANNEL_ID2, CHANNEL_NAME, CHANNEL_DESCRIPTION)
//                val arrive = BitmapFactory.decodeResource(resources,R.drawable.cjarrive_noti)
//                val collect = BitmapFactory.decodeResource(resources,R.drawable.collect_noti)
//                val pendingIntent = noti.getPendingIntent(
//                    CJActivity::class.java,
//                    NOTIFICATION_REQUEST,
//                    msg)
//                when (msg) {
//                    "full" -> noti.notifyBasic(CHANNEL_ID2, NOTIFICATION_ID2,
//                        "Alarm", "택배 도착",
//                        R.drawable.lockstop_noti,arrive, pendingIntent)
//                    "empty" -> noti.notifyBasic(CHANNEL_ID2, NOTIFICATION_ID2,
//                        "Alarm", "택배 수거",
//                        R.drawable.lockstop_noti, collect,pendingIntent)
//                }
//
//            }
//        }
    }

}