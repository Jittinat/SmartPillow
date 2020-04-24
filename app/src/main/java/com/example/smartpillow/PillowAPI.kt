package com.example.smartpillow

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PillowAPI(BASE_URL: String) {

//    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .baseUrl(BASE_URL)
//        .build()

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttp = OkHttpClient.Builder().addInterceptor(logger)
    private val gson = GsonBuilder().setLenient().create()
    private val api = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build()).build()

    fun <T> buildService(serviceType: Class<T>): T {
        return api.create(serviceType)
    }
}