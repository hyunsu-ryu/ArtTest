package com.example.arttest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_servey.*
import org.jetbrains.anko.startActivity

class SurveyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servey)

        btn_send.setOnClickListener {
            startActivity<ResultActivity>(
            )
        }
    }
}