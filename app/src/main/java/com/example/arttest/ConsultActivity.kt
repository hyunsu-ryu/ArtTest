package com.example.arttest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_consult.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConsultActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)

        var retrofit = Retrofit.Builder()
            .baseUrl("http://18.116.146.115:5003")   // 엔드포인트의 서버 주소 넣기 기본 Admin Url임
            .addConverterFactory(GsonConverterFactory.create()) // 여기서 레트로핏 생성
            .build()

        // 레트로핏 인터페이스 객체에 넣는 작업 ( 레프토핏 객체에서 LonginService 를 올려주는 작업임
        var consultService = retrofit.create(ConsultService::class.java)

        btn_submit.setOnClickListener {
            var name = txt_name.text.toString()
            var phone = txt_phone.text.toString()

            var user_id =intent.getStringExtra("user").toString()


            val consult = Consult(name,phone)
            consultService.request_consult(user_id,consult).enqueue(object : Callback<Consult> {
                override fun onFailure(call: Call<Consult>, t: Throwable) {
                    // 실패시 발생하는 코드
                    Toast.makeText(applicationContext, "전송실패! 다시시도해주세요", Toast.LENGTH_SHORT)
                        .show()


                }

                override fun onResponse(call: Call<Consult>, response: Response<Consult>) {
                    // 성공시 발생하는 코드
                    var consult = response.body()   // id, pwd 넣어줌       서버에서 이 2개의 인자를 보내주는 코드가 있어야횜

                    var dialog = AlertDialog.Builder(this@ConsultActivity)
                    dialog.setTitle("전송 성공")
                    dialog.setMessage("감사합니다 빠른시일 내 연락드리겠습니다.")  // ?를 넣어줘야 null 처리 (없을수도잇으니깐
                    dialog.show()


                }

            })

        }

    }



}