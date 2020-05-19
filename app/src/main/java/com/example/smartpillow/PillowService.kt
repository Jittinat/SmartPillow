package com.example.smartpillow

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface PillowService {
    @GET("getdata")
    fun getProperties(): Call<ResponseBody>
}

