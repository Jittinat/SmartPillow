package com.example.smartpillow

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import androidx.core.content.ContextCompat
import android.graphics.drawable.BitmapDrawable

class Test : Service(), LifecycleObserver {

    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    private var timer: CountDownTimer? = null
    lateinit var timerTask: TimerTask
    internal var TAG = "Timers"
    internal var Your_X_SECS = 1
    var mActivity = MainActivity()
    var count = 0
    var r10mins = "4"
    var r5mins =  "4"
    var cur = "4"
    var newr10mins = "4"
    var newr5mins = "4"
    var newcur = "4"

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)

        startTimer()
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun isActivityVisible(): String {
        Log.d("life",ProcessLifecycleOwner.get().lifecycle.currentState.name)
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

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    internal val handler = Handler()

    fun startTimer() {
        //set a new Timer
        val timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, (Your_X_SECS * 120000).toLong()) //will change to every 2 min is 120000 ms
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        timer?.cancel()
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post {
                    //TODO CALL NOTIFICATION FUNC
                    callApi()
                }
            }
        }
    }

    fun callApi() {
        val url = "http://34.87.85.156:8080/"               //Server url
        val restAPI = PillowAPI(url)
        val deviceService = restAPI.buildService(PillowService::class.java)
        val requestCall = deviceService.getProperties()

        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                callApi()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                val stringResponse = response?.body()?.string()
                val gson = GsonBuilder().setPrettyPrinting().create()
                val dt = gson.fromJson(stringResponse, Data::class.java)

/////////////////////////////////////////////////////////////////////////////////////// Real used
                newr10mins = dt.result10Mins
                newr5mins = dt.result5Mins
                newcur = dt.predictCurrent

                if (count < 30) {                           //will change to 60
                    Log.d("count", count.toString())
                    if (r10mins != "4") {                //check first time?
                        if (cur != newcur) {
                            if (newr5mins == newcur) {        //real change
                                count = 0
                                if (cur == "3" || cur == "4") {     //start sleep
                                    creatnotification("sleep",r5mins,newcur)
                                    checksts(newcur, isActivityVisible())  //send cur = 3 or 4
                                } else {
                                    creatnotification("sleep",r5mins,newcur)
                                    checksts(
                                        newcur,
                                        isActivityVisible()
                                    )  //send cur not 3
                                }
                                setvalue()
                            } else {                          //not sure
                                count++
                                setvalue()
                            }
                        } else {                               //real change
                            if (newr5mins != r5mins && newr5mins == newcur) {
                                count = 0
                                if (cur == "3" || cur == "4") {
                                    creatnotification("sleep",r5mins,newcur)
                                    checksts(
                                        newcur,
                                        isActivityVisible()
                                    )  //send cur = 3 or 4
                                } else {
                                    creatnotification("sleep",r5mins,newcur)
                                    checksts(
                                        newcur,
                                        isActivityVisible()
                                    )  //send cur not 3
                                }
                                setvalue()
                            } else if (newcur == "3") {           //status = 3 not count
                                count = 0
                                setvalue()
                            } else {                          //not status 3
                                count++
                                setvalue()
                            }
                        }
                    } else {                                                   //first time
                        setvalue()                                        //welcome page
                    }
                } else {                                        //more than 2 hours
                    //TODO(CHANGE POSTURE THEN COUNT = 0)
                    if (count == 30) {                            //2 hours
                        creatnotification("2 hours",newr10mins,newcur)
                        count++
                        Log.d("count", count.toString())
                    } else if (cur != newcur) {                    //more than 2 hours check change posture
                        if (newr5mins == newcur) {        //real change
                            count = 0
                            creatnotification("sleep",r5mins,newcur)
                            checksts(newcur, isActivityVisible())  //send cur not 3
                            setvalue()
                        } else {                          //not sure
                            count++
                            setvalue()
                            Log.d("count", count.toString())
                        }
                    } else {                               //real change
                        if (newr5mins != r5mins && newr5mins == newcur) {
                            count = 0
                            creatnotification("sleep",r5mins,newcur)
                            checksts(newcur, isActivityVisible())  //send cur not 3
                            count++
                            setvalue()
                        } else {                          //not status 3
                            count++
                            setvalue()
                            Log.d("count", count.toString())
                        }
                    }
                }
            }
        })
    }
/////////////////////////////////////////////////////////////////////////////////////// Real used

    fun creatnotification(sts: String,newr5: String,newc: String) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder =
            NotificationCompat.Builder(applicationContext, default_notification_channel_id)

        val drawable = ContextCompat.getDrawable(this, R.drawable.day)
        val bitmap = (drawable as BitmapDrawable).bitmap

        val drawable2 = ContextCompat.getDrawable(this, R.drawable.day2)
        val bitmap2 = (drawable2 as BitmapDrawable).bitmap


        when (sts){
            "2 hours" ->
            {
                mBuilder.setContentTitle("Have to change posture!!")
                mBuilder.setContentText("Patient doesn't change posture for 2 hours")
                mBuilder.setLargeIcon(bitmap)
            }
            "sleep" ->{
                when (newc) {
                    "0" ->  {
                        when (newr5) {
                            "3" ->  {
                                mBuilder.setContentTitle("Start lay head")
                                mBuilder.setContentText("Patient is start sleeping in supine posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                            else -> {
                                mBuilder.setContentTitle("Lateral to Supine")
                                when (newr5){
                                    "1" -> mBuilder.setContentText("Patient is change from left lateral posture to supine posture")
                                    "2" -> mBuilder.setContentText("Patient is change from right lateral posture to supine posture")
                                }
                                mBuilder.setLargeIcon(bitmap)
                            }
                        }
                    }
                    "3" -> {
                        mBuilder.setContentTitle("No Patient")
                        mBuilder.setContentText("No patient lay head on the pillow")
                        mBuilder.setLargeIcon(bitmap2)
                    }
                    "1" -> {
                        when (newr5) {
                            "0" -> {
                                mBuilder.setContentTitle("Supine to Lateral")
                                mBuilder.setContentText("Patient is change from supine posture to left lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                            "3" -> {
                                mBuilder.setContentTitle("Start lay head")
                                mBuilder.setContentText("Patient is start sleeping in left lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                            "2" -> {
                                mBuilder.setContentTitle("Lateral to Lateral")
                                mBuilder.setContentText("Patient is change from right lateral posture to left lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                        }
                    }
                    "2" -> {
                        when (newr5) {
                            "0" -> {
                                mBuilder.setContentTitle("Supine to Lateral")
                                mBuilder.setContentText("Patient is change from supine posture to right lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                            "3" -> {
                                mBuilder.setContentTitle("Start lay head")
                                mBuilder.setContentText("Patient is start sleeping in right lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                            "2" -> {
                                mBuilder.setContentTitle("Lateral to Lateral")
                                mBuilder.setContentText("Patient is change from left lateral posture to right lateral posture")
                                mBuilder.setLargeIcon(bitmap)
                            }
                        }
                    }
                    else -> {
                        mBuilder.setContentTitle("Sleeping")
                        mBuilder.setContentText("Patient is sleeping")
                        mBuilder.setLargeIcon(bitmap)
                    }
                }
            }
        }

        mBuilder.setTicker("Notification Listener Service Example")
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        mBuilder.setAutoCancel(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    fun setvalue(){
        r10mins = newr10mins
        r5mins = newr5mins
        cur = newcur
    }

    fun checksts(sts: String,isActive: String){
        if(isActive != "CREATED" || isActive == "STARTED") {
            if(sts == "positive"){
                var intent3 = Intent(this, MainActivity::class.java).putExtra("status",1)
                startActivity(intent3)
            }else{                                                  //not 3 mean hava patient on pillow
                var intent2 = Intent(this, MainActivity::class.java).putExtra("status",2)
                startActivity(intent2)
            }
        }else{
            getdata(sts)
        }
    }

    fun getdata(sts: String):String{
        return sts
    }

}