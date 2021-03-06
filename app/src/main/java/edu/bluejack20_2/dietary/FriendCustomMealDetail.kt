package edu.bluejack20_2.dietary

import android.graphics.Color
import android.icu.util.ULocale.getLanguage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class FriendCustomMealDetail(val mealItem: MealItem? = null) : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    private lateinit var menuId: String
    private var hasMeal: Boolean = false
    private var friendid: String = ""
    var language = Locale.getDefault().language

    private lateinit var mealCalories: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_custom_meal_detail)
        menuId = (getIntent().getExtras()?.get("menuId") as String?).toString()
        hasMeal = ((getIntent().getExtras()?.get("hasMeal") as Boolean?)!!)
        friendid = ((getIntent().getExtras()?.get("friendid") as String?)!!)

        if(hasMeal){
            findViewById<Button>(R.id.addthismeal).visibility = View.INVISIBLE
        }else{
            findViewById<Button>(R.id.addthismeal).visibility = View.VISIBLE
        }

        findViewById<ImageView>(R.id.finishh).setOnClickListener{
            finish()
        }

        var curruserid = ""
        db.collection("users").whereEqualTo("username", FirebaseAuth.getInstance().currentUser.displayName).get().addOnSuccessListener {
            if(!it.isEmpty){
                curruserid = it.documents.first().id.toString()
            }
        }

        mealCalories = findViewById(R.id.friendmealCalories)
        val ingredientList = getIngredientList()
        findViewById<RecyclerView>(R.id.friendingredientView).adapter =
            NonEditableIngredientAdapter(ingredientList, this)
        findViewById<RecyclerView>(R.id.friendingredientView).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.friendingredientView).setHasFixedSize(true)

        findViewById<Button>(R.id.addthismeal).setOnClickListener{
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.no)) { dialog, which ->
                    // Respond to negative button press
                }
                .setNegativeButton(getString(R.string.yes)) { dialog, which ->
                    // Respond to positive button press
                    db.collection("CustomMeals").document(menuId).get().addOnSuccessListener {
                        if(it.exists()){
                            db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                db.collection("CustomMeals").document(it.id).update("UserID", curruserid)
                                db.collection("CustomMeals").document(it.id).update("OwnerID", friendid)
                                db.collection("CustomMeals").document(it.id).update("OwnerMenuID", menuId)
                                findViewById<Button>(R.id.addthismeal).visibility = View.INVISIBLE
                            }
                        }
                    }

                }
                .show()
        }

    }

    fun getIngredientList(): MutableList<IngredientItem>?{
        val list = ArrayList<IngredientItem>()
        db.collection("CustomMeals").document(menuId).get().addOnSuccessListener {
            if(it.exists()){
                findViewById<TextView>(R.id.friendmealName).text = it.data?.get("CustomMealName").toString()
                var caloriess = it.data?.get("Calories").toString().toFloat().roundToInt().toString()
                findViewById<TextView>(R.id.friendmealCalories).text = getString(R.string.calories, caloriess)
                val ingredients = it.data?.get("CustomMealIngredients")!! as List<Map<*, *>>
                ingredients.forEach {
                    var calCount = 0F
                    var weight = 0F
                    var name = ""
                    calCount = it["Weight"].toString().toFloat()
                    weight = it["Weight"].toString().toFloat()
                    val ingredientId = it["IngredientID"].toString()
                    db.collection("MainIngredients").document(it["IngredientID"].toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            calCount /= it.data?.get("IngredientsWeight")!!.toString().toFloat()
                            calCount *= it.data?.get("IngredientsCalories")!!.toString().toFloat()
                            calCount = calCount.roundToInt().toFloat()
                            if(language.equals("in")){
                                name = it.data?.get("IngredientsName_in")!!.toString()
                            }else{
                                name = it.data?.get("IngredientsName_en")!!.toString()
                            }
                            list.add(IngredientItem(ingredientId, name, calCount, weight))
                            findViewById<RecyclerView>(R.id.friendingredientView).adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        return list
    }

}