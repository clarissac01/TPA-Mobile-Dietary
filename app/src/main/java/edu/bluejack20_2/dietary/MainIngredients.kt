package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainIngredients : AppCompatActivity() {

    val ingredient = mutableMapOf<String, Int>()
    var mainIngredientsList = mutableListOf<MainIngredientsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ingredients)

        var auth: FirebaseAuth
        var user: FirebaseUser
        val db = FirebaseFirestore.getInstance()
        db.collection("MainIngredients").get().addOnSuccessListener {

            val result: StringBuffer = StringBuffer()

            for (document in it.documents) {
                    val data = MainIngredientsData(
                        document.id,
                        document.data?.getValue("IngredientsName").toString(),
                        document.data?.getValue("IngredientsCalories").toString().toInt(),
                        document.data?.getValue("IngredientsWeight").toString().toInt()
                    )

                mainIngredientsList.add(data)
            }

            Log.wtf("size", mainIngredientsList.size.toString())
            var adapter = MainIngredientsAdapter(mainIngredientsList, ingredient)
            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = adapter
            findViewById<RecyclerView>(R.id.rvMainIngredients).layoutManager =
                LinearLayoutManager(this)
        }

        findViewById<ExtendedFloatingActionButton>(R.id.insert_custom_meal).setOnClickListener {
            Log.wtf("apa", ingredient.toString())
            var totalCal = 0
            auth = FirebaseAuth.getInstance()
            user = auth.currentUser
            val customMealName = intent.extras!!.getString("customMealName")
            for ((ingredientId, quantity) in ingredient) {
                val ingredient = mainIngredientsList.find {
                    it.ingredientsId == ingredientId
                }
                totalCal += ingredient!!.ingredientsCalories * ((quantity * ingredient!!.ingredientsWeight) / ingredient!!.ingredientsWeight)
            }

            val customIngredient = mutableListOf<Map<Any, Any>>()
            for ((ingredientId, quantity) in ingredient) {
                val ingredient = mainIngredientsList.find {
                    it.ingredientsId == ingredientId
                }
                customIngredient.add(mapOf(
                    "IngredientID" to ingredientId,
                    "Weight" to (quantity * ingredient!!.ingredientsWeight)
                ))
            }

            db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
                val userID = it.documents.first().id
                val data = hashMapOf(
                    "UserID" to userID,
                    "CustomMealName" to customMealName,
                    "isCustom" to true,
                    "Calories" to totalCal,
                    "CustomMealIngredients" to customIngredient
                )
                db.collection("CustomMeals").add(data).addOnSuccessListener {
                    Toast.makeText(this, "Success Add Custom Meal", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        }

    }
}