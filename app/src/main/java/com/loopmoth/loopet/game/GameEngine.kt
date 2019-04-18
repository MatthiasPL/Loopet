package com.loopmoth.loopet.game

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import java.util.*
import android.R.string.cancel
import android.content.Context
import com.loopmoth.loopet.creatures.Baby.Loopel
import com.loopmoth.loopet.creatures.CurrentCreature
import android.content.Context.MODE_PRIVATE
import android.util.Log
import java.io.*
import com.google.gson.Gson

class GameEngine: Service() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var mCurrentCreature: CurrentCreature

    private var mTimer: Timer? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        //stworzenie serwisu gry
        super.onCreate()

        mHandler = Handler()

        mCurrentCreature = CurrentCreature(Loopel())

        checkIfFileExists(this@GameEngine)

        if (mTimer != null)
        // Cancel if already existed
            mTimer!!.cancel()
        else
            mTimer = Timer()   //recreate new
        mTimer!!.scheduleAtFixedRate(TimeDisplay(), 0, 10*60*1000)   //Schedule task
        //Toast.makeText(this, "Stworzono", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        //TODO: zapisanie i ewentualne wgranie na serwer
        //Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    internal inner class TimeDisplay : TimerTask() {
        override fun run() {
            // run on another thread
            mHandler.post {
                // display toast
                // tu będzię główna pętla gry
                Toast.makeText(this@GameEngine, "Odświeżanie", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@GameEngine, mCurrentCreature.toJSON(), Toast.LENGTH_LONG).show()
            }
        }
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
        //Toast.makeText(this, "opened", Toast.LENGTH_SHORT).show()
    }

    private fun checkIfFileExists(context: Context){
        val path = context.filesDir
        val file = File(path, "data.json")

        if(file.exists()){
            readData(context)
        }
        else{
            saveData(context)
        }
    }
}