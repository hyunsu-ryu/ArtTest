package com.example.arttest

import retrofit2.Call
import retrofit2.http.*


interface ResultService {
//
    @GET("/get_result/{user_id}")

    fun request_result(
//    /get_result?userid=12 ---> @Get
       @Path("user_id") user_id: String
    //Path

//    ) : retrofit2.Call<Result>     /// 레트로핏 객에체서 <> 안에는 Result.kt 의 (아웃풋 리콜)모델을 제시해줘야함
    ): Call<Result>
}