package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class CustomMealDetail : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val ingredient = mutableMapOf<String, Int>()
    val customMealIngredientList = mutableListOf<CustomMealIngredientData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_meal_detail)

        val customMealId = intent.extras!!.getString("customMealId")

        db.collection("CustomMeals").document(customMealId!!).get().addOnSuccessListener {
            findViewById<MaterialTextView>(R.id.custmeal_name_text).text =
                it.getString("CustomMealName")
            findViewById<MaterialTextView>(R.id.custmeal_cal_text).text = resources.getString(
                R.string.calories,
                it.get("Calories").toString()
            )

            var adapter = CustomMealIngredientsAdapter(customMealIngredientList, ingredient)
            val ingredientsList = it.data?.get("CustomMealIngredients") as List<Map<String, Any>>
            for (ingredient in ingredientsList) {
                var ingredientID = ingredient["IngredientID"]
                db.collection("MainIngredients").document(ingredientID.toString()).get()
                    .addOnSuccessListener {
                        val data = CustomMealIngredientData(
                            it.get("IngredientsName").toString(),
                            it.get("IngredientsCalories").toString().toInt()
                        )
                        customMealIngredientList.add(data)
                        adapter.notifyDataSetChanged()
                    }
            }
            findViewById<RecyclerView>(R.id.rvCustomMealIngredients).adapter = adapter
            findViewById<RecyclerView>(R.id.rvCustomMealIngredients).layoutManager =
                LinearLayoutManager(this)
        }

        findViewById<MaterialButton>(R.id.custmeal_ingredients_btn).setOnClickListener {
            val intent = Intent(this, MainIngredientsUpdateActivity::class.java)
            intent.putExtra("customMealId", customMealId)
            startActivity(intent)
        }
    }
}