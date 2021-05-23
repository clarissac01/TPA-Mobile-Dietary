package edu.bluejack20_2.dietary

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        val darkThemeSwitch = findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                AppCompatDelegate.getDefaultNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        findViewById<Button>(R.id.setting_close_btn).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.set_breakfast_time_btn).setOnClickListener {
            val cal = Calendar.getInstance()
            var userID : String
            val timerSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hourOfDay: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                var hour = cal.get(Calendar.HOUR_OF_DAY)
                var minute = cal.get(Calendar.MINUTE)

                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    userID = it.documents.first().id
                    db.collection("users").document(userID).update("breakfastHour", hour)
                    db.collection("users").document(userID).update("breakfastMinute", minute)
                }
            }
            TimePickerDialog(this, timerSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        findViewById<MaterialButton>(R.id.set_lunch_time_btn).setOnClickListener {
            val cal = Calendar.getInstance()
            var userID : String
            val timerSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hourOfDay: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                var hour = cal.get(Calendar.HOUR_OF_DAY)
                var minute = cal.get(Calendar.MINUTE)

                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    userID = it.documents.first().id
                    db.collection("users").document(userID).update("lunchHour", hour)
                    db.collection("users").document(userID).update("lunchMinute", minute)
                }
            }
            TimePickerDialog(this, timerSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        findViewById<MaterialButton>(R.id.set_dinner_time_btn).setOnClickListener {
            val cal = Calendar.getInstance()
            var userID : String
            val timerSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hourOfDay: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                var hour = cal.get(Calendar.HOUR_OF_DAY)
                var minute = cal.get(Calendar.MINUTE)

                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    userID = it.documents.first().id
                    db.collection("users").document(userID).update("dinnerHour", hour)
                    db.collection("users").document(userID).update("dinnerMinute", minute)
                }
            }
            TimePickerDialog(this, timerSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        findViewById<MaterialButton>(R.id.set_snack_time_btn).setOnClickListener {
            val cal = Calendar.getInstance()
            var userID : String
            val timerSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hourOfDay: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                var hour = cal.get(Calendar.HOUR_OF_DAY)
                var minute = cal.get(Calendar.MINUTE)

                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    userID = it.documents.first().id
                    db.collection("users").document(userID).update("snackHour", hour)
                    db.collection("users").document(userID).update("snackMinute", minute)
                }
            }
            TimePickerDialog(this, timerSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }
}