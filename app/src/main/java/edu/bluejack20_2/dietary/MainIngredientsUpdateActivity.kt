package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class MainIngredientsUpdateActivity : AppCompatActivity() {
    val ingredient = mutableMapOf<String, Int>()
    var mainIngredientsList = mutableListOf<MainIngredientsData>()
    lateinit var search : SearchView
    val filtered  = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ingredients_update)

        var auth: FirebaseAuth
        var user: FirebaseUser
        val db = FirebaseFirestore.getInstance()
        val customMealId = intent.extras!!.getString("customMealId")

        search = findViewById<SearchView>(R.id.search_ingredients)
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

            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    search.clearFocus()
                    var filter = mainIngredientsList?.filter {
                        it.ingredientsName.contains(query.toString(), true)
                    }
                    Log.wtf("ayolah", filter.toString())
                    if(filter.isEmpty()) {
                        filtered.clear()
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter =
                            MainIngredientsAdapter(filter, ingredient)
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter?.notifyDataSetChanged()
                        Toast.makeText(applicationContext, getString(R.string.ingredients_not_found), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        filtered.clear()
                        filter.forEach {
                            filtered[it.ingredientsId] = 0
                        }
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = MainIngredientsAdapter(filter, ingredient)
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter?.notifyDataSetChanged()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    search.clearFocus()
                    if(newText != null && newText.equals("")) {
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = MainIngredientsAdapter(mainIngredientsList, ingredient)
                        findViewById<RecyclerView>(R.id.rvMainIngredients).adapter?.notifyDataSetChanged()
                    }
                    else {
                        var filter = mainIngredientsList?.filter {
                            it.ingredientsName.contains(newText.toString(), true)
                        }
                        if(!filter.isEmpty()) {
                            filtered.clear()
                            filter.forEach {
                                filtered[it.ingredientsId] = 0
                            }
                            Log.wtf("ayolah2", filter.toString())
                            Log.wtf("ayolah2", filtered.toString())
                            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = MainIngredientsAdapter(filter, ingredient)
                            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter?.notifyDataSetChanged()
                        }
                        else {
                            filtered.clear()
                            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter =
                                MainIngredientsAdapter(filter, ingredient)
                            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter?.notifyDataSetChanged()
                            Toast.makeText(applicationContext, getString(R.string.ingredients_not_found), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    return false
                }

            })

            Log.wtf("size", mainIngredientsList.size.toString())
            var adapter = MainIngredientsAdapter(mainIngredientsList, ingredient)
            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = adapter
            findViewById<RecyclerView>(R.id.rvMainIngredients).layoutManager =
                LinearLayoutManager(this)

            db.collection("CustomMeals").document(customMealId!!).get().addOnSuccessListener {
                if(it.exists()) {
                    val customIngredientsList = it.data?.get("CustomMealIngredients") as List<Map<*, *>>?
                    for (mi in customIngredientsList!!) {
                        val hehe = mainIngredientsList.find {
                            it.ingredientsId == mi["IngredientID"]
                        }
                        var qty = mi["Weight"].toString().toFloat() / hehe?.ingredientsWeight.toString().toInt()
                        ingredient[hehe?.ingredientsId!!] = qty.roundToInt()
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }

        findViewById<ExtendedFloatingActionButton>(R.id.update_main_ingredients_btn).setOnClickListener {
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
                    "Calories" to totalCal,
                    "CustomMealIngredients" to customIngredient
                )
                db.collection("CustomMeals").document(customMealId!!).update(data).addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.success_update_custom_meal), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

        }

    }
}