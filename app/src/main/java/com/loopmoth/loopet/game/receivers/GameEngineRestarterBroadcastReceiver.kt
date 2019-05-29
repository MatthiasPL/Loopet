package com.loopmoth.loopet.game.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loopmoth.loopet.game.services.GameEngine
import xdroid.toaster.Toaster.toast
import android.app.AlarmManager
import android.app.PendingIntent



class GameEngineRestarterBroadcastReceiver:BroadcastReceiver {
    constructor()

    val interval = 2000L

    /*override fun onReceive(context: Context?, intent: Intent?) {
        val intentService = Intent(context, GameEngine::class.java)
        toast("włączanie serwisu")
        context!!.startService(intentService)
    }*/

    override fun onReceive(context: Context, intent: Intent) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getService(
            context,
            0,
            Intent(context, GameEngine::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pi)
    }
}