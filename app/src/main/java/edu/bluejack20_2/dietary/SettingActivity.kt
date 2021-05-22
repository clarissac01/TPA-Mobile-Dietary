package edu.bluejack20_2.dietary

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        var picker = findViewById<TimePicker>(R.id.timePicker2)

        val darkThemeSwitch = findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                AppCompatDelegate.getDefaultNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                recreate()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                recreate()
            }
        }

        findViewById<MaterialButton>(R.id.set_time_btn).setOnClickListener {
            picker.setIs24HourView(true)
            val hour: Int
            val minute: Int
            if (Build.VERSION.SDK_INT >= 23) {
                hour = picker.getHour() - 7
                minute = picker.getMinute()
            } else {
                hour = picker.getCurrentHour()
                minute = picker.getCurrentMinute()
            }

            Log.wtf("hour", hour.toString())
            Log.wtf("minute", minute.toString())

            intent.putExtra("hour", hour)
            intent.putExtra("minute", minute)

        }
    }
}