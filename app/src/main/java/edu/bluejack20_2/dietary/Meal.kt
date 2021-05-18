package edu.bluejack20_2.dietary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Boolean.FALSE

class Meal : AppCompatActivity() {

    private lateinit var type:String
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        type = (getIntent().getExtras()?.get("type") as String?).toString()

        findViewById<ImageView>(R.id.back2).setOnClickListener{
            finish()
        }

        val mealList = getMealList()
        findViewById<RecyclerView>(R.id.recommend_meal_view).adapter =
            RecommendMealsAdapter(this, type, mealList, this)
        findViewById<RecyclerView>(R.id.recommend_meal_view).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.recommend_meal_view).setHasFixedSize(true)

        val mealList2 = getMealList2()
        findViewById<RecyclerView>(R.id.custom_meal_view).adapter =
            RecommendMealsAdapter(this, type, mealList2, this)
        findViewById<RecyclerView>(R.id.custom_meal_view).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.custom_meal_view).setHasFixedSize(true)
    }

    fun getMealList(): MutableList<MealItem>?{

        val list = ArrayList<MealItem>()

        //logic get recommended meal

        db.collection("CustomMeals").get().addOnSuccessListener {
            if(!it?.isEmpty!!){
                it.documents.forEach{

                    list.add(MealItem(it.id.toString(), it.get("CustomMealName") as String,
                        it.get("Calories").toString().toFloat(), FALSE
                    ))
                    findViewById<RecyclerView>(R.id.recommend_meal_view).adapter?.notifyDataSetChanged()
                }
            }
        }

        return list
    }

    fun getMealList2(): MutableList<MealItem>?{

        val list = ArrayList<MealItem>()

        //logic get recommended meal

        db.collection("users").whereEqualTo("username", FirebaseAuth.getInstance().currentUser.displayName).get()
            .addOnSuccessListener {
                if(!it?.isEmpty!!){
                    var userid = it.documents.first().id
                    db.collection("CustomMeals").whereEqualTo("UserID", userid).get().addOnSuccessListener {
                        if(!it?.isEmpty!!){
                            it.documents.forEach{

                                list.add(MealItem(it.id.toString(), it.get("CustomMealName") as String,
                                    it.get("Calories").toString().toFloat(), FALSE
                                ))
                                findViewById<RecyclerView>(R.id.custom_meal_view).adapter?.notifyDataSetChanged()
                                var errorMessage: TextView = findViewById(R.id.noCustomMealMessage)
                                errorMessage.visibility = View.INVISIBLE
                            }
                        }
                    }.addOnFailureListener {
                            var errorMessage: TextView = findViewById(R.id.noCustomMealMessage)
                            errorMessage.visibility = View.VISIBLE
                    }
                }
            }


        return list
    }

}