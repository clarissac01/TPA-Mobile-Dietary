package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AddCustomMealActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_custom_meal)

        findViewById<MaterialButton>(R.id.main_ingredients_btn).setOnClickListener {
            val customMealName = findViewById<TextInputEditText>(R.id.custom_meal_name_text)
            val intent = Intent(this, MainIngredients::class.java)
            intent.putExtra("customMealName", customMealName.text.toString())
            customMealName.setText("")
            startActivity(intent)
        }
    }
}