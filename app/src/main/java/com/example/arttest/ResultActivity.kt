package com.example.arttest

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.startActivity
import java.io.File

class ResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        btn_home.setOnClickListener {
            startActivity<MainActivity>(
            )
        }

        IV_PICTURE.setImageBitmap(App.bitmap)


//        result_picture.setImageBitmap(onDownloadClick())

    }

//    fun onDownloadClick(v: View): Bitmap? {
//        downloadWithTransferUtility()
//    }
//
//
//
//    private fun downloadWithTransferUtility() {
//        // Cognito 샘플 코드. CredentialsProvider 객체 생성
//        val credentialsProvider = CognitoCachingCredentialsProvider(
//            applicationContext,
//            "ap-northeast-2:167efb36-dea5-4724-935d-0c419fc48f12", // 자격 증명 풀 ID   다름.
//            Regions.AP_NORTHEAST_2 // 리전
//        )
//
//        // 반드시 호출해야 한다.
//        TransferNetworkLossHandler.getInstance(applicationContext)
//
//        // TransferUtility 객체 생성
//        val transferUtility = TransferUtility.builder()
//            .context(applicationContext)
//            .defaultBucket("Bucket_Name") // 디폴트 버킷 이름.
//            .s3Client(AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
//            .build()
//
//        // 다운로드 실행. object: "SomeFile.mp4". 두 번째 파라메터는 Local경로 File 객체.
//        val downloadObserver = transferUtility.download("SomeFile.mp4", File(filesDir.absolutePath + "/SomeFile.mp4"))
//
//        // 다운로드 과정을 알 수 있도록 Listener를 추가할 수 있다.
//        downloadObserver.setTransferListener(object : TransferListener {
//            override fun onStateChanged(id: Int, state: TransferState) {
//                if (state == TransferState.COMPLETED) {
//                    Log.d("AWS", "DOWNLOAD Completed!")
//                }
//            }
//
//            override fun onProgressChanged(id: Int, current: Long, total: Long) {
//                try {
//                    val done = (((current.toDouble() / total) * 100.0).toInt()) //as Int
//                    Log.d("AWS", "DOWNLOAD - - ID: $id, percent done = $done")
//                }
//                catch (e: Exception) {
//                    Log.d("AWS", "Trouble calculating progress percent", e)
//                }
//            }
//
//            override fun onError(id: Int, ex: Exception) {
//                Log.d("AWS", "DOWNLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
//            }
//        })
//    }

}