package com.example.smartpillow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sts = intent.getIntExtra("status",0)
        setContentView(R.layout.activity_main)

        var TAG = "Timers"

        Log.d("intent",sts.toString())
//        if(count_test == 0) {
//            startService(Intent(this, Test::class.java))
//            count_test++
//            //
//        }

        fun isActivityVisible(): String {
            Log.d("life", ProcessLifecycleOwner.get().lifecycle.currentState.name)
            return ProcessLifecycleOwner.get().lifecycle.currentState.name
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onAppBackgrounded() {
            //App in background

            Log.e(TAG, "************* backgrounded")
            Log.e(TAG, "************* ${isActivityVisible()}")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onAppForegrounded() {

            Log.e(TAG, "************* foregrounded")
            Log.e(TAG, "************* ${isActivityVisible()}")
            // App in foreground
        }


        //TODO(HOW TO START SERVICE ONE TIME ONLY)
        if (isMyServiceRunning(Test::class.java)) {
            //text.setText("Stoped")
            //stopService(Intent(this@MainActivity, Test::class.java))
            Log.d("Stop","Stop")
            Log.d("hi",isMyServiceRunning(Test::class.java).toString())
            getapi()
        } else {
            //text.setText("Started")
            if(isActivityVisible() != "STARTED" && isActivityVisible() != "RESUMED"){
                startService(Intent(this@MainActivity, Test::class.java))
            }
            Log.d("start","startService")
            Log.d("hiho",isMyServiceRunning(Test::class.java).toString())
            getapi()
        }

//        if(sts.toString() == "1"){
//            var intent = Intent(this, TestUi::class.java)
//            startActivity(intent)
//        }else if(sts.toString() == "2"){
//            var intent2 = Intent(this, TestUi2::class.java)
//            startActivity(intent2)
//        }else{
//            setContentView(R.layout.activity_main)
//        }

        //Log.d("main", sts.toString())

//        var intent3 = Intent(this, TestUi::class.java)
//        startActivity(intent3)
    }

    override fun onStop() {
        super.onStop()
        //startService(Intent(this, Test::class.java))
    }

    fun closeApp(view: View) {
        finish()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun getapi(){
        //val url = "http://atilal.com/"
        val url = "http://35.240.207.155:8080/"
        //val url = "https://jsonplaceholder.typicode.com/"
        val restAPI = PillowAPI(url)
        val deviceService = restAPI.buildService(PillowService::class.java)
        val requestCall = deviceService.getProperties()

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
//                Toast.makeText(applicationContext, "Error Occurred jaaaa${t?.message}", Toast.LENGTH_LONG)
//                    .show()
                Log.d("mainpage","api error")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                val stringResponse = response?.body()?.string()
                val gson = GsonBuilder().setPrettyPrinting().create()
                val dt2 = gson.fromJson(stringResponse, Data::class.java)
                val str = dt2.predictCurrent
                Log.d("mainpage",str)
                when (str) {
                    ("3") -> {var intent = Intent(this@MainActivity, TestUi2::class.java)
                        startActivity(intent)
                    }
                    else -> { var intent2 = Intent(this@MainActivity, TestUi::class.java)
                        startActivity(intent2)
                    }
                }
            }
        })
    }
}
