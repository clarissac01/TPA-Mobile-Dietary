package edu.bluejack20_2.dietary.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val title = it.getStringExtra("NOTIFICATION_TITLE")
            val message = it.getStringExtra("NOTIFICATION_MESSAGE")

            val notificationData = Data.Builder()
                .putString("NOTIFICATION_TITLE", title)
                .putString("NOTIFICATION_MESSAGE", message)
                .build()

            val work = OneTimeWorkRequest.Builder(ScheduledWorker::class.java)
                .setInputData(notificationData)
                .build()

            WorkManager.getInstance().beginWith(work).enqueue()

            Log.wtf(javaClass.name, "WorkManager is Enqueued.")
        }
    }
}