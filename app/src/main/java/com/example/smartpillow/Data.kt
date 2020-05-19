package com.example.smartpillow

import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("result10Mins") var  result10Mins: String,
    @SerializedName("result5Mins") var  result5Mins: String,
    @SerializedName("predictCurrent") var predictCurrent: String,
    @SerializedName("TimeStampCurrent") var TimeStampCurrent: String
    )


