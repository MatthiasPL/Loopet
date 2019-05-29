package com.loopmoth.loopet.game.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.loopmoth.loopet.game.receivers.GameEngineRestarterBroadcastReceiver
import xdroid.toaster.Toaster.toast
import java.util.*
import android.R.string.cancel
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.loopmoth.loopet.creatures.CurrentCreature
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class GameEngine() : Service() {

    // constant
    //val NOTIFY_INTERVAL = (15* 60 * 1000).toLong() // 15 minutes interval
    val NOTIFY_INTERVAL = 1000.toLong()

    // run on another Thread to avoid crash
    private val mHandler = Handler()
    // timer handling
    private var mTimer: Timer? = null

    private lateinit var mCurrentCreature: CurrentCreature

    override fun onCreate() {
        super.onCreate()

        checkIfFileExists(this)

        if (mTimer != null) {
            mTimer!!.cancel()
        } else {
            // recreate new
            mTimer = Timer()
        }
        // schedule task
        mTimer!!.scheduleAtFixedRate(TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL)
    }

    internal inner class TimeDisplayTimerTask : TimerTask() {

        private// get date time in custom format
        val dateTime: String
            @SuppressLint("SimpleDateFormat")
            get() {
                val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
                return sdf.format(Date())
            }

        override fun run() {
            // run on another thread
            mHandler.post {
                // display toast
                Toast.makeText(
                    applicationContext, dateTime,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //wywołanie funkcji
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent(this, GameEngineRestarterBroadcastReceiver::class.java))
        toast("usługa wyłączona")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val intent = Intent("com.android.ServiceStopped")
        sendBroadcast(intent)
        toast("Serwis został zabity")
    }

    private fun saveData(context: Context) {
        val path = context.filesDir
        val file = File(path, "data.json")
        val stream = FileOutputStream(file)
        try {
            stream.write(mCurrentCreature.toJSON().toByteArray())
        } finally {
            stream.close()
            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
    }
}

    private fun readData(context: Context){
        val path = context.filesDir
        val file = File(path, "data.json")
        val length = file.length() //as Int

        val bytes = ByteArray(length.toInt())

        val `in` = FileInputStream(file)
            try {
                `in`.read(bytes)
            } finally {
                `in`.close()
        }

        val contents = String(bytes)

        //Toast.makeText(this, path.toString(), Toast.LENGTH_SHORT).show()

        val gson = Gson()
        mCurrentCreature = gson.fromJson(contents, CurrentCreature::class.java)
        //Toast.makeText(this, contents, Toast.LENGTH_SHORT).show()
    }

    private fun checkIfFileExists(context: Context){
        val path = context.filesDir
        val file = File(path, "data.json")

        if(file.exists()){
            readData(context)
        }
        else{
            saveData(context)
            readData(context)
        }
    }
}