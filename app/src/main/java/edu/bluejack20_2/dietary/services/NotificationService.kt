package edu.bluejack20_2.dietary.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import edu.bluejack20_2.dietary.R
import java.util.*
import kotlin.math.abs

class NotificationService: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val uuid = UUID.randomUUID()
        val builder =
            NotificationCompat.Builder(context!!, uuid.toString())
                .setSmallIcon(R.drawable.default_icon)
                .setContentTitle("Dietary")
                .setContentText("Hi, Time to Log Your Meal! :D")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val name: CharSequence = "DietaryChannel"
        val description = "Channel for Dietary Reminder"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(uuid.toString(), name, importance)
        channel.description = description

        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(abs(Random().nextInt()), builder!!.build())
        Log.i("NotificationService", "Alarm Called!")
    }
}