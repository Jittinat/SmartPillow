package com.example.smartpillow

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*


interface PillowService {
    //@GET("cookies.php")
    @GET("getdata")
    //@GET("todos/1")
    fun getProperties(): Call<ResponseBody>
    //fun getProperties(@Path("path") path: String): Observable<Data>
}

