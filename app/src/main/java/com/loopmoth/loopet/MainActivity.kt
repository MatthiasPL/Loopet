package com.loopmoth.loopet

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopmoth.loopet.game.services.GameEngine
import android.os.PowerManager
import android.os.Build
import android.os.Vibrator
import android.provider.Settings
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.telephony.SmsManager
import android.util.Log


class MainActivity : AppCompatActivity() {

    var mServiceIntent: Intent? = null
    private var mGameEngine: GameEngine? = null

    private lateinit var ctx: Context

    fun getCtx(): Context {
        return ctx
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        setContentView(R.layout.activity_main)
        mGameEngine = GameEngine()
        mServiceIntent = Intent(getCtx(), mGameEngine!!::class.java)
        if (!isMyServiceRunning(mGameEngine!!::class.java)) {
            startService(mServiceIntent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }



    /*private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

    fun createConstraints() = Constraints.Builder()
        //.setRequiredNetworkType(NetworkType.UNMETERED)  // if connected to WIFI
        // other values(NOT_REQUIRED, CONNECTED, NOT_ROAMING, METERED)
        .setRequiresBatteryNotLow(true)                 // if the battery is not low
        //.setRequiresStorageNotLow(true)                 // if the storage is not low
        .build()

    fun createWorkRequest(data: Data) = PeriodicWorkRequestBuilder<GameEngine>(15, TimeUnit.MINUTES)  // setting period to 12 hours
        // set input data for the work
        .setInputData(data)
        .setConstraints(createConstraints())
        // setting a backoff on case the work needs to retry
        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
        .build()

    fun startWork() {
        // set the input data, it is like a Bundle
        val work = createWorkRequest(Data.EMPTY)

        /* enqueue a work, ExistingPeriodicWorkPolicy.KEEP means that if this work already existits, it will be kept
        if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced */
        WorkManager
            .getInstance()
            .enqueueUniquePeriodicWork("Game engine", ExistingPeriodicWorkPolicy.REPLACE, work)

        WorkManager.getInstance().getWorkInfoByIdLiveData(work.id)
            .observe(this, Observer { workInfo ->
                // Check if the current work's state is "successfully finished"
                if(workInfo!=null && workInfo.state==WorkInfo.State.RUNNING){
                    Toast.makeText(this, "działa", Toast.LENGTH_SHORT).show()
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(500)
                }
            })
    }*/

    private fun sendSMS(phoneNumber: String, message: String) {
        val sentPendingIntents = ArrayList<PendingIntent>()
        val deliveredPendingIntents = ArrayList<PendingIntent>()
        try {
            val sms = SmsManager.getDefault()
            val mSMSMessage = sms.divideMessage(message)
            sms.sendMultipartTextMessage(
                phoneNumber, "508979891", mSMSMessage,
                null, null
            )

        } catch (e: Exception) {

            e.printStackTrace()
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG).show()
        }

    }
}
