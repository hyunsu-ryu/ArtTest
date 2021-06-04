package com.example.arttest

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import android.graphics.Bitmap


class App : Application() {
    companion object {
        var bitmap: Bitmap? =null
    }
    override fun onCreate() {
        super.onCreate()

    }
}



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btn_start.setOnClickListener {
            startActivity<LoginActivity>(
            )
        }



    }








}