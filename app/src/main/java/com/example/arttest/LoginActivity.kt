package com.example.arttest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
        logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
        logo?.setDisplayUseLogoEnabled(true)
        logo?.setDisplayShowHomeEnabled(true)

        var retrofit = Retrofit.Builder()
            .baseUrl("http://18.116.146.115:5002")   // 엔드포인트의 서버 주소 넣기 기본 Admin Url임
            .addConverterFactory(GsonConverterFactory.create()) // 여기서 레트로핏 생성
            .build()

        // 레트로핏 인터페이스 객체에 넣는 작업 ( 레프토핏 객체에서 LonginService 를 올려주는 작업임
        var loginService = retrofit.create(LoginService::class.java)



        btn_login.setOnClickListener {
            var id = id_login.text.toString()
            var pwd = pwd_login.text.toString()


//





            // 서버 DB에서 그에 맞는 값들을 가져온다.
            val login = Login(id, pwd)
            loginService.requestLogin(login).enqueue(object : Callback<Login>{
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    // 실패시 발생하는 코드
                    t.printStackTrace()  // 시스템 에러를 뽑아준다.
                    Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    // 성공시 발생하는 코드
//                    Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT)
//                        .show()
                    var Login = response.body()   // id, pwd 넣어줌       서버에서 이 2개의 인자를 보내주는 코드가 있어야횜

                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("메시지")
                    dialog.setMessage("로그인되었습니다.")  // ?를 넣어줘야 null 처리 (없을수도잇으니깐
                    dialog.show()

                }

            })

        }

        btn_pictureactivity.setOnClickListener {

            var id = id_login.text.toString()           // 얘는 인텐트 구성한 다음에 넘기는 방식이다!

            val nextIntent = Intent(this@LoginActivity, PictureActivity::class.java)
            nextIntent.putExtra("user_id",id) // 추가하고싶으면 밑에 하나 더 추가하면됨

            startActivity(nextIntent)           //앙코랑 같은 역할을 하게됨

//            startActivity<PictureActivity>(       // 얘는 앙코 액티비티임!
//
//            )
        }

        btn_newmake.setOnClickListener {
            startActivity<MakeActivity>(
            )
        }
    }
}