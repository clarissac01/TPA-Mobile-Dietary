package edu.bluejack20_2.dietary

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.services.AlarmReceiver
import edu.bluejack20_2.dietary.services.NotificationService
import edu.bluejack20_2.dietary.services.friendpage.add_friend.AddFriend
import edu.bluejack20_2.dietary.services.home_page.HomeFragment
import edu.bluejack20_2.dietary.services.login.LoginActivity
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    val remind = Reminder(this)

    override fun onStart() {
//        FirebaseAuth.getInstance().signOut()


        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        updateUI(account)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        } else {
            val account = GoogleSignIn.getLastSignedInAccount(this)
        }


//        FirebaseMessaging.getInstance().subscribeToTopic("discount-offers")
//            .addOnCompleteListener { task ->
//                Toast.makeText(this, "Hello! Time to log your meal!", Toast.LENGTH_SHORT).show()
//                if (!task.isSuccessful) {
//                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
//                }
//            }


        val homeFragment =
            HomeFragment()
        val profileFragment = ProfileFragment()
        val customMealFragment = CustomMealFragment()
        val friendsFragment = FriendsFragment()
        val journeyFragment = JourneyFragment()
        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).bringToFront()

        val nav = findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear)

        setCurrentFragment(homeFragment)
        nav.setNavigationChangeListener { view, position ->
            when (view.id) {
                R.id.l_item_home -> setCurrentFragment(homeFragment)
                R.id.l_item_custom_meal -> setCurrentFragment(customMealFragment)
                R.id.l_item_profile -> setCurrentFragment(profileFragment)
                R.id.l_item_friend -> setCurrentFragment(friendsFragment)
                R.id.l_item_journey -> setCurrentFragment(journeyFragment)
            }
        }

//        Log.wtf("current", nav.currentActiveItemPosition.toString())
//        nav.setCurrentActiveItem(0)

//        createNotificationChannel()
//        setNotification()
        remind.setNotification()
    }

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//        }
//    }

    private fun setNotification() {
        val intent = Intent(this, NotificationService::class.java)
        val breakfastAlarm = PendingIntent.getBroadcast(this, abs(Random().nextInt()), intent, 0)
        val lunchAlarm = PendingIntent.getBroadcast(this, abs(Random().nextInt()), intent, 0)
        val dinnerAlarm = PendingIntent.getBroadcast(this, abs(Random().nextInt()), intent, 0)
        val snackAlarm = PendingIntent.getBroadcast(this, abs(Random().nextInt()), intent, 0)
        var userID: String
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val hour = intent.extras?.getString("hour")
        val minute = intent.extras?.getString("minute")
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        Log.wtf("hour", hour)
        Log.wtf("minute", minute)

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

                            val alarmManager2 =
                                getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                            val pendingIntent =
                                PendingIntent.getService(
                                    this, 0, intent,
                                    PendingIntent.FLAG_NO_CREATE
                                )
                            if (pendingIntent != null && alarmManager2 != null) {
                                alarmManager.cancel(pendingIntent)
                            }

                            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val alarmIntent =
                                Intent(this, AlarmReceiver::class.java).let { intent ->
                                    PendingIntent.getBroadcast(this, 0, intent, 0)
                                }

                            val calendarBreakfast: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (breakfastHour != null && breakfastMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, breakfastHour)
                                    set(Calendar.MINUTE, breakfastMinute)
                                }
                            }
                            val calendarLunch: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (lunchHour != null && lunchMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, lunchHour)
                                    set(Calendar.MINUTE, lunchMinute)
                                }
                            }
                            val calendarDinner: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (dinnerHour != null && dinnerMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, dinnerHour)
                                    set(Calendar.MINUTE, dinnerMinute)
                                }
                            }
                            val calendarSnack: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                if (snackHour != null && snackMinute != null) {
                                    set(Calendar.HOUR_OF_DAY, snackHour)
                                    set(Calendar.MINUTE, snackMinute)
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


        Log.i("MainActivity", "Notification Set!")

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, abs(Random().nextInt()), intent, 0)
        }

// Set the alarm to start at 8:30 a.m.
        val call: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 58)
        }

        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            call.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }


    fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }


    fun gotoMainIngredients(view: View) {
        startActivity(Intent(this, MainIngredients::class.java))
    }

    fun gotoEditProfile(view: View) {
        startActivity(Intent(this, EditProfile::class.java))
    }

    fun addFriend(view: View) {
        startActivity(Intent(this, AddFriend::class.java))
    }
}