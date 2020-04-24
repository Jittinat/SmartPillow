package com.example.smartpillow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class TestUi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_ui)

        val sts = intent.getIntExtra("status",0)
        Log.d("main", sts.toString())

    }
}
