package edu.bluejack20_2.dietary.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.bluejack20_2.dietary.R

class NotificationService: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val builder =
            NotificationCompat.Builder(context!!, "GabyChannelID")
                .setSmallIcon(R.drawable.default_icon)
                .setContentTitle("Dietary")
                .setContentText("Hi, we missed you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(200, builder!!.build())
        Log.i("NotificationService", "Alarm Called!")
    }
}