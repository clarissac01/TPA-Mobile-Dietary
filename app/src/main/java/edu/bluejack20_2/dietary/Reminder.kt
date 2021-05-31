package edu.bluejack20_2.dietary

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.services.AlarmReceiver
import edu.bluejack20_2.dietary.services.NotificationService
import java.util.*
import kotlin.math.abs

class Reminder(val activity: Activity) {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()


    val intent = Intent(activity, NotificationService::class.java)
    val breakfastAlarm = PendingIntent.getBroadcast(activity, 7, intent, 0)
    val lunchAlarm = PendingIntent.getBroadcast(activity, 77, intent, 0)
    val dinnerAlarm = PendingIntent.getBroadcast(activity, 777, intent, 0)
    val snackAlarm = PendingIntent.getBroadcast(activity, 7777, intent, 0)

    fun setNotification() {
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var userID: String
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        Log.wtf("username", user.displayName)
        if (auth.currentUser != null) {
            db.collection("users").whereEqualTo("username", user.displayName).get()
                .addOnFailureListener { f -> Log.wtf("hehe", f.toString()) }
                .addOnSuccessListener {
                    if (it.documents.isEmpty()) {
                        return@addOnSuccessListener
                    } else {
                        userID = it.documents.first().id
                        db.collection("users").document(userID).get().addOnSuccessListener {
                            var breakfastHour = it.getLong("breakfastHour").toString().toInt()
                            var breakfastMinute = it.getLong("breakfastMinute").toString().toInt()
                            var lunchHour = it.getLong("lunchHour").toString().toInt()
                            var lunchMinute = it.getLong("lunchMinute").toString().toInt()
                            var dinnerHour = it.getLong("dinnerHour").toString().toInt()
                            var dinnerMinute = it.getLong("dinnerMinute").toString().toInt()
                            var snackHour = it.getLong("snackHour").toString().toInt()
                            var snackMinute = it.getLong("snackMinute").toString().toInt()

                            val calendarBreakfast: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (breakfastHour != null && breakfastMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, breakfastHour)
                                    set(Calendar.MINUTE, breakfastMinute)
                                    set(Calendar.SECOND, 0)
                                }
                            }
                            val calendarLunch: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (lunchHour != null && lunchMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, lunchHour)
                                    set(Calendar.MINUTE, lunchMinute)
                                    set(Calendar.SECOND, 0)
                                }
                            }
                            val calendarDinner: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (dinnerHour != null && dinnerMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, dinnerHour)
                                    set(Calendar.MINUTE, dinnerMinute)
                                    set(Calendar.SECOND, 0)
                                }
                            }
                            val calendarSnack: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (snackHour != null && snackMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, snackHour)
                                    set(Calendar.MINUTE, snackMinute)
                                    set(Calendar.SECOND, 0)
                                }
                            }

                            listOf(
                                breakfastAlarm to calendarBreakfast,
                                lunchAlarm to calendarLunch,
                                dinnerAlarm to calendarDinner,
                                snackAlarm to calendarSnack
                            ).forEach {
                                alarmManager.cancel(it.first)
                                alarmManager.setAlarmClock(
                                    AlarmManager.AlarmClockInfo(
                                        it.second.timeInMillis,
                                        it.first
                                    ),
                                    it.first
                                )
                            }
                        }
                    }
                }
        }


//        Log.i("MainActivity", "Notification Set!")
//
//        val alarmMgr = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(activity, AlarmReceiver::class.java).let { intent ->
//            PendingIntent.getBroadcast(activity, abs(Random().nextInt()), intent, 0)
//        }
//
//// Set the alarm to start at 8:30 a.m.
//        val call: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 23)
//            set(Calendar.MINUTE, 58)
//        }
//
//        alarmMgr?.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            call.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            alarmIntent
//        )
    }
}