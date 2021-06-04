package com.example.arttest

import retrofit2.http.*


// 인풋 -- > 정보를 서버에 전달해주는 역할임      인풋 작성 이후 레트로핈 객체 생성해주기

interface LoginService{
// get  가져오는것 , post insert 하는 것!!!!!!!!!!!

    /// 여기서 하는 작업은 아이디를 확인하는 과정임

//    @FormUrlEncoded     // 서버 URL Encode a=b&b=c
    @POST("/login")   // 배경화면 다음의 url 주소를 적어서 접근함
    fun requestLogin(
        // 서버에서 id =userid라는 부분이 생성됨 이부분을 토대로 retrofit 응답함함
        @Body login: Login
//        @Field("id") id: String,            // input 정보는 안드로이드(클라이언트개발) -> 서버(개발)로 보내주는 값임(중요)
//        @Field("pwd") pwd: String
    ) : retrofit2.Call<Login>



//    @FormUrlEncoded
    // 서버가 JSON 인코딩 { a: b }
    @POST("/register")
    fun request_newmake(
        @Body newmake: Newmake
//        @Body("id") id: String,
//        @Field("pwd") pwd: String,
//        @Field("sex") sex: Int,
//        @Field("age") age: Int
    ) : retrofit2.Call<Newmake>  /// 레트로핏 객에체서 <> 안에는 Login.kt 의 모델을 제시해줘야함
}