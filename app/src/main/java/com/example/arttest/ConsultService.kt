package com.example.arttest

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ConsultService {

    @POST("/counsel/{user_id}")

    fun request_consult(
            @Path("user_id") user_id: String,
            @Body Consult: Consult

    ) : retrofit2.Call<Consult>
}