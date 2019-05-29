//package com.loopmoth.loopet.game
//
//import android.app.Service
//import android.content.Intent
//import android.content.res.Configuration
//import android.widget.Toast
//import java.util.*
//import android.R.string.cancel
//import android.app.PendingIntent
//import android.content.Context
//import com.loopmoth.loopet.creatures.Baby.Loopel
//import com.loopmoth.loopet.creatures.CurrentCreature
//import android.content.Context.MODE_PRIVATE
//import android.net.Uri
//import android.os.*
//import android.telephony.SmsManager
//import android.util.Log
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.work.ListenableWorker
//import androidx.work.Operation
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.firebase.jobdispatcher.JobParameters
//import com.firebase.jobdispatcher.JobService
//import java.io.*
//import com.google.gson.Gson
//import com.loopmoth.loopet.MainActivity
//import xdroid.toaster.Toaster.toast
//import xdroid.toaster.Toaster.toastLong
//
//class GameEngine(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
//
//    private lateinit var mCurrentCreature: CurrentCreature
//
//    override fun doWork(): Result {
//        //toastLong("działa")
//        //sendSMS("+48508979891", "działa")
//        //val vibratorService = getSystemService(mContext.VIBRATOR_SERVICE) as Vibrator
//        //vibratorService.vibrate(2500)
//        mCurrentCreature = CurrentCreature(Loopel())
//        checkIfFileExists(applicationContext)
//        //return Result.success()
//        return Result.success()
//    }
//
//    private fun saveData(context: Context) {
//        val path = context.filesDir
//        val file = File(path, "data.json")
//        val stream = FileOutputStream(file)
//        try {
//            stream.write(mCurrentCreature.toJSON().toByteArray())
//        } finally {
//            stream.close()
//            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun readData(context: Context){
//        val path = context.filesDir
//        val file = File(path, "data.json")
//        val length = file.length() //as Int
//
//        val bytes = ByteArray(length.toInt())
//
//        val `in` = FileInputStream(file)
//        try {
//            `in`.read(bytes)
//        } finally {
//            `in`.close()
//        }
//
//        val contents = String(bytes)
//
//        //Toast.makeText(this, path.toString(), Toast.LENGTH_SHORT).show()
//
//        val gson = Gson()
//        mCurrentCreature = gson.fromJson(contents, CurrentCreature::class.java)
//        //Toast.makeText(this, contents, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun checkIfFileExists(context: Context){
//        val path = context.filesDir
//        val file = File(path, "data.json")
//
//        //toastLong("sprawdzono")
//        //Toast.makeText(context, "Checking if file exists", Toast.LENGTH_SHORT).show()
//
//        if(file.exists()){
//            readData(context)
//        }
//        else{
//            saveData(context)
//            readData(context)
//        }
//    }
//
//    fun sendSMS(smsNumber: String, sms: String) {
//        val smsIntent = Intent(Intent.ACTION_SENDTO)
//        smsIntent.data = Uri.parse(smsNumber)
//        smsIntent.putExtra("sms_body", sms)
//
//        val scAddress: String? = null
//        val sentIntent: PendingIntent? = null
//        val deliveryIntent: PendingIntent? = null
//
//        val smsManager = SmsManager.getDefault()
//        smsManager.sendTextMessage(
//            smsNumber, scAddress, sms,
//            sentIntent, deliveryIntent
//        )
//    }
//}