package com.example.arttest

// 아웃풋 생성
// 서버에서 통신 호출시 응답값을 받아오는 곳

/// 로그인 시 받아오는 응답들  -- 얘낸 Django의 View에서 통신모듈로 지정되어있어야 함

// 서버에서 id, pwd라는 녀석으로 나에게 돌려줄 것임
data class Login(
    var id:String,              // 얘내 4개의인자는 서버에서의 Output 변수들임 !!!!!!
    var pwd:String    //
)


data class Newmake(
    var id: String,
    var pwd: String,
    var sex: Int,
    var age: Int
)