package com.example.arttest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_newmake.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmake)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)


        var retrofit = Retrofit.Builder()
            .baseUrl("http://18.116.146.115:5002")   // 엔드포인트의 서버 주소 넣기 기본 Admin Url임
            .addConverterFactory(GsonConverterFactory.create()) // 여기서 레트로핏 생성
            .build()

        // 레트로핏 인터페이스 객체에 넣는 작업 ( 레프토핏 객체에서 LonginService 를 올려주는 작업임
        var makeService = retrofit.create(LoginService::class.java)

        btn_make.setOnClickListener {
            var id = id_new.text.toString()
            var pwd = pwd_new.text.toString()
            var sex = sex_new.text.toString().toInt()
            var age = age_new.text.toString().toInt()

            val nextIntent =Intent(this,SurveyActivity::class.java)
            nextIntent.putExtra("age",age)
            startActivity(nextIntent)

//            Log.d("MakeActivity", "$Id, $Pwd, $Sex, $Age")
            // 서버 DB에서 그에 맞는 값들을 가져온다.
            val newmake = Newmake(id, pwd, sex, age)
            makeService.request_newmake(newmake).enqueue(object : Callback<Newmake> {
                override fun onFailure(call: Call<Newmake>, t: Throwable) {
                    // 실패시 발생하는 코드

                    Toast.makeText(applicationContext, "회원가입 실패", Toast.LENGTH_SHORT)
                        .show()

//                    var dialog = AlertDialog.Builder(this@NewmakeActivity)
//                    dialog.setTitle("알람")
//                    dialog.setMessage("register fail")
//                    dialog.show()

                }

                override fun onResponse(call: Call<Newmake>, response: Response<Newmake>) {
                    // 성공시 발생하는 코드
                    var newmake = response.body()   // id, pwd 넣어줌       서버에서 이 2개의 인자를 보내주는 코드가 있어야횜

                    Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT)
                        .show()


//                    var dialog = AlertDialog.Builder(this@NewmakeActivity)
//                    dialog.setTitle("알람")
//                    dialog.setMessage("register Complete")  // ?를 넣어줘야 null 처리 (없을수도잇으니깐
//                    dialog.show()
                }

            })

        }





        btn_loginactivity.setOnClickListener {
            startActivity<LoginActivity>(
            )
        }
    }
}