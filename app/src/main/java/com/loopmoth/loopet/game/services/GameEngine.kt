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
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.loopmoth.loopet.R
import com.loopmoth.loopet.creatures.Adult.Looyang
import com.loopmoth.loopet.creatures.Adult.Looying
import com.loopmoth.loopet.creatures.Baby.Loopel
import com.loopmoth.loopet.creatures.Dead
import com.loopmoth.loopet.creatures.Old.Loolong
import com.loopmoth.loopet.creatures.Young.Loochi
import com.loopmoth.loopet.creatures.Young.Loohan
import com.loopmoth.loopet.enums.Stadium
import java.io.*


class GameEngine() : Service() {

    //TODO: OPTIONAL: illnesses, happiness

    companion object {
        var CREATURE_NAME: String = "com.loopmoth.loopet.game.services.GameEngine.CREATURE_NAME"
        var CREATURE_HUNGRY: String = "com.loopmoth.loopet.game.services.GameEngine.CREATURE_HUNGRY"
        val CREATURE_SLEEPY: String = "com.loopmoth.loopet.game.services.GameEngine.CREATURE_SLEEPY"
        val CREATURE_POOP: String = "com.loopmoth.loopet.game.services.GameEngine.CREATURE_POOP"
        val GAME_RESULT = "com.loopmoth.loopet.game.services.GameEngine.REQUEST_PROCESSED"
    }

    val GAME_RESULT = "com.loopmoth.loopet.game.services.GameEngine.REQUEST_PROCESSED"

    // constant
    val minutes: Double = 0.01//20
    val NOTIFY_INTERVAL = (minutes * 60 * 1000).toLong() // 20 minutes interval
    //val NOTIFY_INTERVAL = 1000.toLong() // temporary for testing

    // run on another Thread to avoid crash
    private val mHandler = Handler()
    // timer handling
    private var mTimer: Timer? = null

    private var mCurrentCreature: CurrentCreature? = null

    private lateinit var broadcaster: LocalBroadcastManager

    private val fileName: String = "data.json"

    override fun onCreate() {
        super.onCreate()

        //Sprawdzenie, czy istnieje już instancja potworka w formacie json
        checkIfFileExists(this)

        //timer, który działa w tle i wykonuje się co określony czas
        //sprawdzamy, czy istnieje
        if (mTimer != null) {
            mTimer!!.cancel()
        } else {
            // recreate new
            mTimer = Timer()
        }
        // schedule task
        mTimer!!.scheduleAtFixedRate(GameEngineTimerTask(), 0, NOTIFY_INTERVAL)
        //tworzymy broadcastera
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    //wysyłanie danych do widoku
    fun sendResult() {
        val intent = Intent(GAME_RESULT)

        intent.putExtra(CREATURE_NAME, mCurrentCreature!!.name.toString())
        intent.putExtra(CREATURE_HUNGRY, mCurrentCreature!!.hunger.toString())
        intent.putExtra(CREATURE_SLEEPY, mCurrentCreature!!.age.toString())
        intent.putExtra(CREATURE_POOP, mCurrentCreature!!.poop.toString())

        //wysyłamy broadcasta
        broadcaster.sendBroadcast(intent)
    }

    //task silnika gry
    internal inner class GameEngineTimerTask : TimerTask() {
        override fun run() {
            // run on another thread
            mHandler.post {
                //tutaj działa silnik całej gry
                gameEngineLoop()
                sendResult()
            }
        }
    }

    //funkcja wykonująca określone zadania związane z aktualizowaniem statusu stworka, wysyła też powiadomienia,
    //gdy są one wymagane
    private fun gameEngineLoop(){
        if(mCurrentCreature!!.stadium != Stadium.DEAD){
            checkPrivateNeeds()
            sendNotificationIfNeeded()
            checkEvolving()
        }
    }

    //aktualizowanie głodu, snu i innych potrzeb fizjologicznych
    private fun checkPrivateNeeds(){

        //wyliczanie ile dni ma stworek, dodajemy deltę czasu z poniższego wzoru
        val deltaAge: Double = 100 * (1 / ((60/minutes) * 24)).toDouble()

        val rightNow = Calendar.getInstance()
        val currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY)

        addAlertWhenNeeded() //zaniedbanemu stworkowi się to nie podoba

        if(!mCurrentCreature!!.is_sleeping){
            mCurrentCreature!!.hunger -= 10
            mCurrentCreature!!.poop -= 10

            if(mCurrentCreature!!.hunger < 0){
                mCurrentCreature!!.hunger = 0
                mCurrentCreature!!.is_hungry = true
            }

            if(mCurrentCreature!!.poop < 0){
                mCurrentCreature!!.poop = 0
                mCurrentCreature!!.has_pooped = true
            }

            if(currentHourIn24Format >= 22){
                mCurrentCreature!!.is_sleepy = true
                mCurrentCreature!!.Sleep()
            }
            else if (currentHourIn24Format >=9){
                mCurrentCreature!!.is_sleepy = false
                mCurrentCreature!!.Sleep()
            }
        }

        mCurrentCreature!!.GetOlder(deltaAge)
    }

    //sprawdzenie czy stworek może ewoluować
    private fun checkEvolving(){
        //ewolucja Loopel
        if (mCurrentCreature!!.name == "Loopel"){
            if (mCurrentCreature!!.age > 0.1 && mCurrentCreature!!.care_mistakes < 1){
                val tempAge = mCurrentCreature!!.age
                mCurrentCreature = CurrentCreature(Loochi())
                mCurrentCreature!!.age = tempAge
            }
            else if (mCurrentCreature!!.age > 0.1 && mCurrentCreature!!.care_mistakes >= 1){
                val tempAge = mCurrentCreature!!.age
                mCurrentCreature = CurrentCreature(Loohan())
                mCurrentCreature!!.age = tempAge
            }
        }

        //ewolucja Loochi
        else if (mCurrentCreature!!.name == "Loochi"){
            if (mCurrentCreature!!.age > 0.2){
                val tempAge = mCurrentCreature!!.age
                mCurrentCreature = CurrentCreature(Looying())
                mCurrentCreature!!.age = tempAge
            }
        }

        //ewolucja Loohan
        else if (mCurrentCreature!!.name == "Loohan"){
            if (mCurrentCreature!!.age > 0.2){
                val tempAge = mCurrentCreature!!.age
                mCurrentCreature = CurrentCreature(Looyang())
                mCurrentCreature!!.age = tempAge
            }
        }

        //ewolucja Looying/Looyang
        else if (mCurrentCreature!!.name == "Looying" || mCurrentCreature!!.name == "Looyang"){
            if (mCurrentCreature!!.age > 0.3){
                val tempAge = mCurrentCreature!!.age
                mCurrentCreature = CurrentCreature(Loolong())
                mCurrentCreature!!.age = tempAge
            }
        }

        else if(mCurrentCreature!!.age > 5){
            val tempAge = mCurrentCreature!!.age
            mCurrentCreature = CurrentCreature(Dead())
            mCurrentCreature!!.age = tempAge
        }

        resetStats()
    }

    private fun resetStats(){
        mCurrentCreature!!.care_mistakes = 0
        mCurrentCreature!!.sleep_alert = 0
        mCurrentCreature!!.poop_alert = 0
        mCurrentCreature!!.hunger_alert = 0
    }

    //zmiana flagi potrzeb stworka
    private fun addAlertWhenNeeded(){
        if(!mCurrentCreature!!.is_sleeping){
            if(mCurrentCreature!!.is_hungry){
                mCurrentCreature!!.HungerAlert()
            }
            if(mCurrentCreature!!.is_sleepy){
                mCurrentCreature!!.SleepAlert()
            }
            if(mCurrentCreature!!.has_pooped){
                mCurrentCreature!!.PoopAlert()
            }
        }
    }

    //wysyłanie powiadomienia, kiedy należy
    private fun sendNotificationIfNeeded(){
        if(mCurrentCreature!!.has_pooped || mCurrentCreature!!.is_hungry || mCurrentCreature!!.is_sleepy ||
            mCurrentCreature!!.is_ill || mCurrentCreature!!.is_dead || mCurrentCreature!!.is_sad){
                sendNotification()
        }
    }

    //sprawdzenie czy plik istnieje
    private fun checkIfFileExists(context: Context){
        val path = context.filesDir
        val file = File(path, fileName)

        file.delete()

        if(file.exists()){
            readData(context)
        }
        else{
            mCurrentCreature = CurrentCreature(Loopel())
            saveData(context)
            readData(context)
        }
    }

    //funkcja wysyłająca powiadomienia
    private fun sendNotification() {
        val mBuilder = NotificationCompat.Builder(this)

        val intent = Intent(this@GameEngine, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        mBuilder.setContentIntent(pendingIntent)

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        mBuilder.setContentTitle("Your " + mCurrentCreature?.name + " needs you!")
        mBuilder.setContentText("Tap the notification to check what is going on.")

        val mNotificationManager =

            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(1, mBuilder.build())

        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(10)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //stworzenie usługi w trybie sticky
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