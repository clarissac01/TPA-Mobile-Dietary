package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.roundToInt

class BMIActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_m_i)

        if(intent.getStringExtra("type") == "Maintain") {
            maintainWeight()
        }
        else if(intent.getStringExtra("type") == "Gain") {
            gainWeight()
        }
        else if(intent.getStringExtra("type") == "Lose") {
            loseWeight()
        }

    }

    fun loseWeight() {
        findViewById<Button>(R.id.calculate_btn).setOnClickListener {
            var weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            var height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            var age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            var bmr = 0.0

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }

            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2 - 500).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375 - 500).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55 - 500).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else {
                Amr.amrValue = (bmr * 1.9 - 500).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
        }
    }

    fun maintainWeight() {
        findViewById<Button>(R.id.calculate_btn).setOnClickListener {
            var weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            var height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            var age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            var bmr = 0.0

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }

            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else {
                Amr.amrValue = (bmr * 1.9).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
        }
    }

    fun gainWeight() {
        findViewById<Button>(R.id.calculate_btn).setOnClickListener {
            var weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            var height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            var age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            var bmr = 0.0
            var amr = 0

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }

            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2 + 400).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375 + 400).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55 + 400).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
            else {
                Amr.amrValue = (bmr * 1.9 + 400).roundToInt()
                Log.wtf("amr", Amr.amrValue.toString())
            }
        }
    }
}