package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Tasks
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.roundToInt

class BMIActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_m_i)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

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
            var weight: Float
            var height: Float
            var age: Int
            if(findViewById<TextInputEditText>(R.id.weight_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.weight_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.height_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.height_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.age_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.age_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            }

            var bmr = 0.0
            val now = com.google.firebase.Timestamp.now().toDate().toInstant()
            val zoneId = TimeZone.getDefault().toZoneId()
            val endDate = LocalDateTime.ofInstant(now, zoneId).plusMonths(1).toInstant(OffsetDateTime.ofInstant(now, zoneId).offset).let {
                com.google.firebase.Timestamp(Date.from(it))
            }

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else if (findViewById<RadioButton>(R.id.radio_male).isChecked == true) {
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                Toast.makeText(this, getString(R.string.gender_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2 - 500).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "LoseWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                }
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375 - 500).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "LoseWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55 - 500).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "LoseWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if (findViewById<RadioButton>(R.id.radio_very_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.9 - 500).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "LoseWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, getString(R.string.active_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    fun maintainWeight() {
        findViewById<Button>(R.id.calculate_btn).setOnClickListener {
            var weight: Float
            var height: Float
            var age: Int
            if(findViewById<TextInputEditText>(R.id.weight_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.weight_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.height_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.height_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.age_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.age_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            }

            var bmr = 0.0
            val now = com.google.firebase.Timestamp.now().toDate().toInstant()
            val zoneId = TimeZone.getDefault().toZoneId()
            val endDate = LocalDateTime.ofInstant(now, zoneId).plusMonths(1).toInstant(OffsetDateTime.ofInstant(now, zoneId).offset).let {
                com.google.firebase.Timestamp(Date.from(it))
            }

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else if (findViewById<RadioButton>(R.id.radio_male).isChecked == true) {
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                Toast.makeText(this, getString(R.string.gender_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "MaintainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "MaintainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)

                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "MaintainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if (findViewById<RadioButton>(R.id.radio_very_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.9).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "MaintainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)

                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, getString(R.string.active_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    fun gainWeight() {
        findViewById<Button>(R.id.calculate_btn).setOnClickListener {
            var weight: Float
            var height: Float
            var age: Int
            if(findViewById<TextInputEditText>(R.id.weight_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.weight_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                weight = findViewById<TextInputEditText>(R.id.weight_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.height_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.height_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                height = findViewById<TextInputEditText>(R.id.height_text).text.toString().toFloat()
            }
            if (findViewById<TextInputEditText>(R.id.age_text).text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.age_empty_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                age = findViewById<TextInputEditText>(R.id.age_text).text.toString().toInt()
            }

            var bmr = 0.0
            val now = com.google.firebase.Timestamp.now().toDate().toInstant()
            val zoneId = TimeZone.getDefault().toZoneId()
            val endDate = LocalDateTime.ofInstant(now, zoneId).plusMonths(1).toInstant(OffsetDateTime.ofInstant(now, zoneId).offset).let {
                com.google.firebase.Timestamp(Date.from(it))
            }

            if(findViewById<RadioButton>(R.id.radio_female).isChecked == true) {
                bmr = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else if (findViewById<RadioButton>(R.id.radio_male).isChecked == true){
                bmr = (66.47 + (13.75 * weight) + (5.003 * height) - (6.755 + age))
                Log.wtf("bmr", bmr.toString())
            }
            else {
                Toast.makeText(this, getString(R.string.gender_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(findViewById<RadioButton>(R.id.radio_sedentary).isChecked == true) {
                Amr.amrValue = (bmr * 1.2 + 400).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "GainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)

                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if(findViewById<RadioButton>(R.id.radio_light_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.375 + 400).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "GainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)

                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if(findViewById<RadioButton>(R.id.radio_moderate_active).isChecked == true) {
                Amr.amrValue = (bmr * 1.55 + 400).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "GainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)
                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else if (findViewById<RadioButton>(R.id.radio_very_active).isChecked == true){
                Amr.amrValue = (bmr * 1.9 + 400).roundToInt()
                var plan = hashMapOf<String, Any>(
                    "CaloriePerDay" to Amr.amrValue,
                    "name" to "GainWeight",
                    "startDate" to com.google.firebase.Timestamp.now(),
                    "endDate" to endDate
                )
                db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                    var userid = it.documents.first().id
                    db.collection("users").document(it.documents.first().id).update("plan", plan)

                    db.collection("CustomMeals").orderBy("day").get().addOnSuccessListener {
                        if(!it.isEmpty){
                            it.documents.forEach {
                                if(it.get("UserID") == null){
                                    db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                        Tasks.whenAll(
                                            db.collection("CustomMeals").document(it.id).update("UserID", userid)
                                        ).addOnSuccessListener {
                                        }
                                    }
                                }
                            }
                            Toast.makeText(this, "Success Add Plan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, getString(R.string.active_not_checked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }
}