package edu.bluejack20_2.dietary

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import android.view.View
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack20_2.dietary.services.NotificationService
import java.util.*

class MainActivity : AppCompatActivity() {

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

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }else{
            val account = GoogleSignIn.getLastSignedInAccount(this)
        }


        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val customMealFragment = CustomMealFragment()
        val friendsFragment = FriendsFragment()
        val journeyFragment = JourneyFragment()
        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).bringToFront()

        setCurrentFragment(homeFragment)

        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).setNavigationChangeListener {view, position ->
            when(view.id) {
                R.id.l_item_home -> setCurrentFragment(homeFragment)
                R.id.l_item_custom_meal -> setCurrentFragment(customMealFragment)
                R.id.l_item_profile -> setCurrentFragment(profileFragment)
                R.id.l_item_friend -> setCurrentFragment(friendsFragment)
                R.id.l_item_journey -> setCurrentFragment(journeyFragment)
            }
        }

//        findViewById<Button>(R.id.main_ingredients_button).setOnClickListener {
//            gotoMainIngredients(it)
//        }

        createNotificationChannel()
        setNotification()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "DietaryChannel"
            val description = "Channel for Dietary Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("GabyChannelID", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java);
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotification() {
        val intent = Intent(this, NotificationService::class.java)
        val breakfastAlarm = PendingIntent.getBroadcast(this, 1, intent, 0)
        val lunchAlarm = PendingIntent.getBroadcast(this, 2, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val hour = intent.extras?.getString("hour")
        val minute = intent.extras?.getString("minute")

        Log.wtf("hour", hour)
        Log.wtf("minute", minute)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if(hour != null && minute != null) {
                set(Calendar.HOUR_OF_DAY, hour.toString().toInt())
                set(Calendar.MINUTE, minute.toString().toInt())
            }
        }

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            breakfastAlarm
        )

        Log.i("MainActivity", "Notification Set!")
    }



    fun setCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }


    fun gotoMainIngredients(view: View) {
        startActivity(Intent(this, MainIngredients::class.java) )
    }

    fun gotoEditProfile(view: View) {
        startActivity(Intent(this, EditProfile::class.java))
    }

    fun addFriend(view: View) {
        startActivity(Intent(this, AddFriend::class.java))
    }
}