package com.example.arttest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_servey.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servey)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)

        var retrofit = Retrofit.Builder()
            .baseUrl("http://18.116.146.115:5001")   // 엔드포인트의 서버 주소 넣기 기본 Admin Url임
            .addConverterFactory(GsonConverterFactory.create()) // 여기서 레트로핏 생성
            .build()

        // 레트로핏 인터페이스 객체에 넣는 작업 ( 레프토핏 객체에서 LonginService 를 올려주는 작업임
        var surveyService = retrofit.create(SurveyService::class.java)


        btn_update.setOnClickListener {

            var user_id =intent.getStringExtra("user").toString()    // 회원가입에서 넘겨받은값임

            var A1 = A1.text.toString().toInt()
            var A2 = A2.text.toString().toInt()
//            var A3 = age.toInt()                    // 회원가입 보여주면 알아서 넘거가게
//            var A3 = A3.text.toString().toInt()  // 항목 입력(기존꺼)



            // 서버 DB에서 그에 맞는 값들을 가져온다.


            val survey = Survey(A1,A2)
            surveyService.request_Survey(user_id,survey).enqueue(object : Callback<Survey> {
                override fun onFailure(call: Call<Survey>, t: Throwable) {
                    // 실패시 발생하는 코드
                    Toast.makeText(applicationContext, "전송실패 !", Toast.LENGTH_SHORT)
                        .show()


                }

                override fun onResponse(call: Call<Survey>, response: Response<Survey>) {
                    // 성공시 발생하는 코드
                    var survey = response.body()   // id, pwd 넣어줌       서버에서 이 2개의 인자를 보내주는 코드가 있어야횜

                    Toast.makeText(applicationContext, "확인되었습니다. 결과보기를 누르세요", Toast.LENGTH_SHORT)
                        .show()


                }

            })
        }


        btn_send.setOnClickListener {
            var id =intent.getStringExtra("user").toString()

            val resultIntent = Intent(this@SurveyActivity,ResultActivity::class.java)
            resultIntent.putExtra("user",id)
            startActivity(resultIntent)

//            startActivity<ResultActivity>(
//            )
        }
    }
}