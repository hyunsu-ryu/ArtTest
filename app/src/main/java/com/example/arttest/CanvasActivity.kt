package com.example.arttest

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.ajithvgiri.canvaslibrary.CanvasView
import kotlinx.android.synthetic.main.activity_canvas.*
import kotlinx.android.synthetic.main.activity_picture.*
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CanvasActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.arttest.R.layout.activity_canvas)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)

        val parentView = findViewById<RelativeLayout>(com.example.arttest.R.id.parentView)
        val canvasView = CanvasView(this)
        parentView.addView(canvasView)

        btn_gobefore.setOnClickListener {

//            var user =intent.getStringExtra("user").toString()
//
//            val pictureIntent = Intent(this@CanvasActivity,PictureActivity::class.java)
//            pictureIntent.putExtra("user_name",user)
//            startActivity(pictureIntent)

            startActivity<PictureActivity>(
            )
        }

        btn_reset.setOnClickListener {
            canvasView.clearCanvas ()
        }

        btn_savepicture.setOnClickListener {
            saveCanvas(parentView.drawToBitmap())
        }

    }
    private fun saveCanvas(bitmap: Bitmap){

//        val bitmap = parentView.drawToBitmap()  // 여기서 bitmap 인자를 만들어 주는게 아니라 35라인에서 만들어줘야함!!
        val folderpath= Environment.getExternalStorageDirectory().absolutePath +"/Pictures/"
        val folder = File(folderpath)


        val timestamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())   // 시간기준으로 저장함
        val fileName ="${timestamp}.jpeg"

//        val saveFile = File(folderpath, fileName)   // 이게 같은역할
//        val FOS: FileOutputStream? = null
//        folder.mkdirs()
        if(!folder.isDirectory){
        folder.mkdirs()                         // 해당 경로에 자동으로 폴더 만들기
        }

        val out =FileOutputStream(folderpath+fileName)

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)

        Toast.makeText(applicationContext, "완료! 이전메뉴-> 갤러리에서 사진을 가져오세요", Toast.LENGTH_SHORT)
            .show()

    }


}
