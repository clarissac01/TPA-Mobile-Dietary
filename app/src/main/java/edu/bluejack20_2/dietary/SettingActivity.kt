package edu.bluejack20_2.dietary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val darkThemeSwitch = findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.isChecked = true
        }
//        darkThemeSwitch.setOnClickListener {
//            Log.wtf("ehhe", "hehe")
//            if (darkThemeSwitch.isChecked) {
////                AppCompatDelegate.getDefaultNightMode()
////                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }
//            else {
////                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }

        darkThemeSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                Toast.makeText(this, "checked", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.getDefaultNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                recreate()
            } else {
                Toast.makeText(this, "not checked", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                recreate()
            }
        }
    }
}