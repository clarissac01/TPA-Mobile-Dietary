package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SnackFragment(var currDay: Int) : Fragment() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var menuName: TextView
    private lateinit var calCount: TextView
    private lateinit var menuId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_snack, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuName = view.findViewById(R.id.menuName)
        calCount = view.findViewById(R.id.calCount)

        initMenu()
        view.findViewById<ConstraintLayout>(R.id.snackLayout).setOnClickListener{
            val intent = Intent(
                context,
                MealDetail::class.java
            )
            intent.putExtra("menuId", menuId)
            intent.putExtra("currentDay", currDay)
            context?.startActivity(
                intent
            )
        }

        var userId = ""

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(!it.isEmpty){
                userId = it.documents.first().id
                db.collection("Journey").whereEqualTo("userID", userId).whereEqualTo("Date", Timestamp.now()).get().addOnSuccessListener {
                    if(!it?.isEmpty!!){
                        if(it.documents.first().get("dinnerMenu")!= null){
                            view.findViewById<Button>(R.id.changeSnack).visibility = View.INVISIBLE
                            view.findViewById<Button>(R.id.floatingActionButton5).visibility = View.INVISIBLE
                        }
                    }else{
                        view.findViewById<Button>(R.id.changeSnack).visibility = View.VISIBLE
                        view.findViewById<Button>(R.id.floatingActionButton5).visibility = View.VISIBLE
                    }
                }

            }
        }

        view.findViewById<Button>(R.id.changeSnack).setOnClickListener {
            val intent = Intent(
                context,
                Meal::class.java
            )
            intent.putExtra("type", "Snack")
            context?.startActivity(
                intent
            )
        }

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton5).setOnClickListener{
            var menucal = 0
            val customIngredient = mutableListOf<Map<*, *>>()
            Tasks.whenAll(
                db.collection("CustomMeals").document(menuId).get().addOnSuccessListener {
                    if(it.exists()){
                        menucal = it.get("Calories").toString().toInt()
                        var inglist = it.get("CustomMealIngredients") as List<Map<*, *>>
                        inglist.forEach {
                            customIngredient.add(mapOf(
                                "ingredientID" to it["IngredientID"],
                                "weight" to it["Weight"]
                            ))
                        }
                    }
                }
            ).addOnSuccessListener {
                db.collection("Journey").whereEqualTo("userID", userId).whereEqualTo("Date", Timestamp.now()).get().addOnSuccessListener {
                    if(!it.isEmpty){
                        var journeyid = it.documents.first().id
                        db.collection("Journey").document(journeyid).get().addOnSuccessListener {
                            if(it.exists()){
                                var totalCal = menucal + it.get("totalCalories").toString().toInt()
                                db.collection("Journey").document(journeyid).update(
                                    "snackMenu", hashMapOf(
                                        "calories" to menucal,
                                        "ingredients" to customIngredient,
                                        "menuID" to menuId
                                    )
                                ).addOnSuccessListener {
                                    db.collection("Journey").document(journeyid).update("totalCalories", totalCal)
                                    view.findViewById<FloatingActionButton>(R.id.floatingActionButton5).visibility = View.INVISIBLE
                                    view.findViewById<FloatingActionButton>(R.id.changeSnack).visibility = View.INVISIBLE
                                }

                            }
                        }
                    }else{
                        db.collection("Journey").add(
                            hashMapOf(
                                "userID" to userId,
                                "totalCalories" to menucal,
                                "Date" to Timestamp.now(),
                                "snackMenu" to hashMapOf(
                                    "calories" to menucal,
                                    "ingredients" to customIngredient,
                                    "menuID" to menuId
                                )
                            )
                        ).addOnSuccessListener {
                            view.findViewById<FloatingActionButton>(R.id.floatingActionButton5).visibility = View.INVISIBLE
                            view.findViewById<FloatingActionButton>(R.id.changeSnack).visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    fun initMenu(){

        db.collection("users").whereEqualTo("username", user.displayName).addSnapshotListener() { it, _ ->
            if(!it?.isEmpty!!){
                val getMapping = it.documents.first().get("plan") as Map<*, *>
                    menuId = getMapping["snackMenu"].toString()
                db.collection("CustomMeals").document(menuId).addSnapshotListener() { it, _ ->
                    if(it?.exists()!!){
                        menuName.text = it.getString("CustomMealName")
                        calCount.text = it.get("Calories").toString() + " kcal"
                    }
                }
            }
        }


    }
}