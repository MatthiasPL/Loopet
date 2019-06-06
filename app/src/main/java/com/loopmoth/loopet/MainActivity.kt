package com.loopmoth.loopet

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.loopmoth.loopet.game.services.GameEngine
import android.provider.Settings
import android.content.BroadcastReceiver
import android.os.*
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import xdroid.toaster.Toaster.toast

class MainActivity : AppCompatActivity() {
    var mServiceIntent: Intent? = null
    private var mGameEngine: GameEngine? = null

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        }
    }

    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        setContentView(R.layout.activity_main)
        mGameEngine = GameEngine()
        mServiceIntent = Intent(ctx, mGameEngine!!::class.java)
        if (!isMyServiceRunning(mGameEngine!!::class.java)) {
            startService(mServiceIntent)
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val name = intent.getStringExtra(GameEngine.CREATURE_NAME)
                val is_hungry = intent.getStringExtra(GameEngine.CREATURE_HUNGRY)
                val is_sleepy = intent.getStringExtra(GameEngine.CREATURE_SLEEPY)
                val has_pooped = intent.getStringExtra(GameEngine.CREATURE_POOP)
                tvName.text = name.toString()
                tvHungry.text = is_hungry
                tvPoop.text = has_pooped
                tvSleepy.text = is_sleepy
            }
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
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
            IntentFilter(GameEngine.GAME_RESULT)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onStop()
    }
}
