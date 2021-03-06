package edu.bluejack20_2.dietary.services.home_page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.MealItem
import edu.bluejack20_2.dietary.R
import edu.bluejack20_2.dietary.services.home_page.adapter.RecommendMealsAdapter
import java.lang.Boolean.FALSE
import java.util.*
import kotlin.collections.ArrayList

class Meal : AppCompatActivity() {

    private lateinit var type:String
    private var currentDay:Int = 0
    var db = FirebaseFirestore.getInstance()
    var language = Locale.getDefault().language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        type = (getIntent().getExtras()?.get("type") as String?).toString()
        currentDay = (getIntent().getExtras()?.get("currentDay") as Int)

        findViewById<ImageView>(R.id.back2).setOnClickListener{
            finish()
        }

        val mealList = getMealList()
        findViewById<RecyclerView>(R.id.recommend_meal_view).adapter =
            RecommendMealsAdapter(
                currentDay,
                this,
                type,
                mealList,
                this
            )
        findViewById<RecyclerView>(R.id.recommend_meal_view).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.recommend_meal_view).setHasFixedSize(true)

        val mealList2 = getMealList2()
        findViewById<RecyclerView>(R.id.custom_meal_view).adapter =
            RecommendMealsAdapter(
                currentDay,
                this,
                type,
                mealList2,
                this
            )
        findViewById<RecyclerView>(R.id.custom_meal_view).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.custom_meal_view).setHasFixedSize(true)
    }

    fun getMealList(): MutableList<MealItem>?{

        val list = ArrayList<MealItem>()
        val list2 = ArrayList<MealItem>()
        val list3 = ArrayList<MealItem>()
        //logic get recommended meal

        Tasks.whenAll(
            db.collection("users").whereEqualTo("username", FirebaseAuth.getInstance().currentUser.displayName).get().addOnSuccessListener {
                if(!it.isEmpty){
                    var userid = it.documents.first().id
                    db.collection("CustomMeals").whereEqualTo("type", type).whereEqualTo("UserID", userid).orderBy("day").get().addOnSuccessListener {
                        if(!it?.isEmpty!!){
                            it.documents.forEach{
                                if(it.get("isCustom") == null){
                                    Log.wtf("is not empty", "ada loh")
                                    if(language.equals("in")){
                                        list.add(
                                            MealItem(
                                                it.id.toString(), it.get("CustomMealName_in") as String,
                                                it.get("Calories").toString().toFloat(), FALSE
                                            )
                                        )
                                    }else{
                                        list.add(
                                            MealItem(
                                                it.id.toString(), it.get("CustomMealName_en") as String,
                                                it.get("Calories").toString().toFloat(), FALSE
                                            )
                                        )
                                    }

                                }
                            }
                            for (i in currentDay+1 until list.size){
                                list2.add(list.get(i))
                            }
                            for (i in 0 until currentDay){
                                list2.add(list.get(i))
                            }
                            for(i in 0 until 5){
                                if(list2.get(i)!=null){
                                    list3.add(list2.get(i))
                                }
                                findViewById<RecyclerView>(R.id.recommend_meal_view).adapter?.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }

        ).addOnSuccessListener {


        }


        return list3
    }

    fun getMealList2(): MutableList<MealItem>?{

        val list = ArrayList<MealItem>()

        //logic get recommended meal

        db.collection("users").whereEqualTo("username", FirebaseAuth.getInstance().currentUser.displayName).get()
            .addOnSuccessListener {
                if(!it?.isEmpty!!){
                    var userid = it.documents.first().id
                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("isCustom", true).get().addOnSuccessListener {
                        if(!it?.isEmpty!!){
                            it.documents.forEach{
                                list.add(
                                    MealItem(
                                        it.id.toString(), it.get("CustomMealName") as String,
                                        it.get("Calories").toString().toFloat(), FALSE
                                    )
                                )
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