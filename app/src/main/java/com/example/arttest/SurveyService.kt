package com.example.arttest


import retrofit2.http.*

interface SurveyService {

    @POST("/user_data/{user_id}")

    fun request_Survey(
        @Path("user_id") user_id: String,
        @Body Survey: Survey


    ) : retrofit2.Call<Survey>     /// 레트로핏 객에체서 <> 안에는 Login.kt 의 모델을 제시해줘야함
}
