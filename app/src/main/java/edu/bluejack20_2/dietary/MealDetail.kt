package edu.bluejack20_2.dietary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class MealDetail : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    private lateinit var menuId: String
    private lateinit var mealCalories: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)
        menuId = (getIntent().getExtras()?.get("menuId") as String?).toString()

        findViewById<ImageView>(R.id.back).setOnClickListener{
            finish()
        }

        mealCalories = findViewById(R.id.mealCalories)
        val ingredientList = getIngredientList()
        findViewById<RecyclerView>(R.id.ingredientView).adapter =
            IngredientAdapter(mealCalories, ingredientList, this)
        findViewById<RecyclerView>(R.id.ingredientView).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.ingredientView).setHasFixedSize(true)

        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            var list: MutableList<Map<*, *>> = mutableListOf()
            var totalCal: Float = 0F
            ingredientList?.forEach {
                var ingredient = mapOf<Any, Any>("IngredientID" to it.ingredientId, "Weight" to it.weight)
                totalCal += it.calory
                list.add(ingredient)

            }
            db.collection("CustomMeals").document(menuId).update(
                hashMapOf(
                    "CustomMealIngredients" to list,
                    "Calories" to totalCal))
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal Update Success", Toast.LENGTH_LONG)
            }
        }

    }

    fun getIngredientList(): MutableList<IngredientItem>?{
        val list = ArrayList<IngredientItem>()
        db.collection("CustomMeals").document(menuId).get().addOnSuccessListener {
            if(it.exists()){
                findViewById<TextView>(R.id.mealName).text = it.data?.get("CustomMealName").toString()
                findViewById<TextView>(R.id.mealCalories).text = it.data?.get("Calories").toString() + " kcal"
                Log.wtf("this meal calories", it.data?.get("Calories").toString())
                val ingredients = it.data?.get("CustomMealIngredients")!! as List<Map<*, *>>
                var calCount = 0F
                var weight = 0F
                var name = ""
                ingredients.forEach {
                    calCount = it["Weight"].toString().toFloat()
                    weight = it["Weight"].toString().toFloat()
                    val ingredientId = it["IngredientID"].toString()
                    db.collection("MainIngredients").document(it["IngredientID"].toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            calCount /= it.data?.get("IngredientsWeight")!!.toString().toFloat()
                            calCount *= it.data?.get("IngredientsCalories")!!.toString().toFloat()
                            calCount = calCount.roundToInt().toFloat()
                            name = it.data?.get("IngredientsName")!!.toString()
                            list.add(IngredientItem(ingredientId, name, calCount, weight))
                            findViewById<RecyclerView>(R.id.ingredientView).adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        return list
    }

}