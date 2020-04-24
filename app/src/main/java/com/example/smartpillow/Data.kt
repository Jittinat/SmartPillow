package com.example.smartpillow

import androidx.annotation.IntegerRes
import com.google.gson.annotations.SerializedName
import android.R.id
import android.R.id.message
import android.R.id.title


data class Data (
    @SerializedName("userId") var userId: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("completed") var completed: String,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: String,
    @SerializedName("result10Mins") var  result10Mins: String,
    @SerializedName("result5Mins") var  result5Mins: String,
    @SerializedName("predictCurrent") var predictCurrent: String,
    @SerializedName("TimeStampCurrent") var TimeStampCurrent: String
    )


