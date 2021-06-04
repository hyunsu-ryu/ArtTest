package com.example.arttest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class ResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)

        // ResultService 호출
        // 받은 결과를 result_content에 설정
        // 써보자

        btn_home.setOnClickListener {
            startActivity<PictureActivity>(
            )
        }

        btn_consult.setOnClickListener{
            var id =intent.getStringExtra("user").toString()

            val consultIntent = Intent(this@ResultActivity,ConsultActivity::class.java)
            consultIntent.putExtra("user",id)
            startActivity(consultIntent)

//            startActivity<ConsultActivity>(
//            )
        }


        IV_PICTURE.setImageBitmap(App.bitmap)
        show_result()

    }

    private fun show_result() {
//        var result = result_content.text.toString()


        var retrofit = Retrofit.Builder()
            .baseUrl("http://18.116.146.115:5001")   // 엔드포인트의 서버 주소 넣기 기본 Admin Url임
            .addConverterFactory(GsonConverterFactory.create()) // 여기서 레트로핏 생성
            .build()

        // 레트로핏 인터페이스 객체에 넣는 작업 ( 레프토핏 객체에서 ResultService 를 올려주는 작업임
        var resultService = retrofit.create(ResultService::class.java)


        var user_id =intent.getStringExtra("user").toString()
        // Application 객체에서 id를 추출해서 userid에 지정

//        var result_id = 1

        resultService.request_result(user_id).enqueue(object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) {
                // 실패시 발생하는 코드

                Toast.makeText(applicationContext, "결과 가져오기 실패", Toast.LENGTH_SHORT)
                    .show()
                result_content.text = "결과값 가져오기 실패했습니다."
            }



            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                // 성공시 발생하는 코드
                Toast.makeText(applicationContext, "결과 성공", Toast.LENGTH_SHORT)
                    .show()

                var response = response.body()          // result 객체
                result_content.text = "설명" +response?.result


            }

        })

    }


}
