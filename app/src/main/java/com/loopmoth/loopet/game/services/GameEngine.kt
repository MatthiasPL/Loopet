package com.loopmoth.loopet.game.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.loopmoth.loopet.game.receivers.GameEngineRestarterBroadcastReceiver
import xdroid.toaster.Toaster.toast
import java.util.*
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.loopmoth.loopet.MainActivity
import com.loopmoth.loopet.creatures.CurrentCreature
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.loopmoth.loopet.R
import com.loopmoth.loopet.creatures.Baby.Loopel
import java.io.*


class GameEngine() : Service() {

    companion object {
        var BUS = MutableLiveData<Object>()
        var COPA_MESSAGE: String = "com.loopmoth.loopet.game.services.GameEngine.COPA_MSG"
        val COPA_RESULT = "com.loopmoth.loopet.game.services.GameEngine.REQUEST_PROCESSED"
    }

    val COPA_RESULT = "com.loopmoth.loopet.game.services.GameEngine.REQUEST_PROCESSED"

    val COPA_MESSAGE = "com.loopmoth.loopet.game.services.GameEngine.COPA_MSG"

    // constant
    //val NOTIFY_INTERVAL = (15* 60 * 1000).toLong() // 15 minutes interval
    val NOTIFY_INTERVAL = 1000.toLong() // temporary for testing

    // run on another Thread to avoid crash
    private val mHandler = Handler()
    // timer handling
    private var mTimer: Timer? = null

    private var mCurrentCreature: CurrentCreature? = null

    private lateinit var broadcaster: LocalBroadcastManager

    private val fileName: String = "data.json"

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
        mTimer!!.scheduleAtFixedRate(GameEngineTimerTask(), 0, NOTIFY_INTERVAL)

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    fun sendResult(message: String?) {
        val intent = Intent(COPA_RESULT)
        if (message != null)
            intent.putExtra(COPA_MESSAGE, message)
        broadcaster.sendBroadcast(intent)
    }

    internal inner class GameEngineTimerTask : TimerTask() {
        override fun run() {
            // run on another thread
            mHandler.post {
                //tutaj działa silnik całej gry
                sendResult(mCurrentCreature!!.name)

                sendNotification()
            }
        }
    }

    private fun checkIfFileExists(context: Context){
        val path = context.filesDir
        val file = File(path, fileName)

        if(file.exists()){
            readData(context)
        }
        else{
            mCurrentCreature = CurrentCreature(Loopel())
            saveData(context)
            readData(context)
        }
    }

    private fun sendNotification() {
        val mBuilder = NotificationCompat.Builder(this)

        val intent = Intent(this@GameEngine, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        mBuilder.setContentIntent(pendingIntent)

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        mBuilder.setContentTitle("Your " + mCurrentCreature?.name + " needs you!")
        mBuilder.setContentText("Tap the notification to check what is going.")

        val mNotificationManager =

            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(1, mBuilder.build())
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
        val file = File(path, fileName)

        file.bufferedWriter().use { out ->
            out.write(mCurrentCreature!!.toJSON())
        }
    }

    private fun readData(context: Context){
        val path = context.filesDir
        val file = File(path, fileName)

        val inputStream: InputStream = file.inputStream()

        val inputString = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        mCurrentCreature = gson.fromJson(inputString, CurrentCreature::class.java)
    }

}