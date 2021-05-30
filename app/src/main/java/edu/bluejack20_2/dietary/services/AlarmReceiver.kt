package edu.bluejack20_2.dietary.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    override fun onReceive(context: Context, intent: Intent) {
            // Set the alarm here.
            Log.wtf("masuk sini dong", "oke siap otw")
            db.collection("users").whereEqualTo("username", user.displayName).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        if (it.documents.first().get("plan") != null) {
                            val getMapping = it.documents.first().get("plan") as Map<*, *>
                            val date1 = Date()
                            val date2 = getMapping["startDate"] as Timestamp
                            var diff = date1.time - date2.toDate().time
                            var res = java.util.concurrent.TimeUnit.DAYS.convert(
                                diff,
                                java.util.concurrent.TimeUnit.MILLISECONDS
                            )
                            res.toString().toInt()
                            var userid = it.documents.first().id
                            db.collection("Journey").whereEqualTo("day", res)
                                .whereEqualTo("userID", userid).get().addOnSuccessListener {
                                    if (it.isEmpty) {
                                        //make the journey collection

                                        db.collection("Journey").add(
                                            mapOf(
                                                "Date" to Timestamp.now(),
                                                "day" to res,
                                                "userID" to userid
                                            )
                                        ).addOnSuccessListener {
                                            Log.wtf("this is the journey id", it.id)
                                            var journeyId = it.id

                                            //get breakfast menu

                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                .whereEqualTo("type", "Breakfast").get().addOnSuccessListener {
                                                    if(!it.isEmpty){
                                                        Log.wtf("inside", it.documents.toString())
                                                        var menuId = it.documents.first().id
                                                        var inglist = mutableListOf<Map<*, *>>()
                                                        var cal = it.documents.first().get("Calories")
                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                            Log.wtf("ada sesuatu", ingredients.toString())
                                                        ingredients?.forEach {
                                                            inglist.add(mapOf(
                                                                "ingredientID" to it["IngredientID"],
                                                                "weight" to it["Weight"]
                                                            ))
                                                        }
                                                        db.collection("Journey").document(journeyId).update(
                                                            mapOf(
                                                                "totalCalories" to cal,
                                                                "breakfastMenu" to mapOf(
                                                                    "calories" to cal,
                                                                    "ingredients" to inglist,
                                                                    "menuID" to menuId
                                                                )
                                                            )
                                                        ).addOnSuccessListener {
                                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                                .whereEqualTo("type", "Lunch").get().addOnSuccessListener {
                                                                    if(!it.isEmpty){
                                                                        Log.wtf("inside", it.documents.toString())
                                                                        var menuId = it.documents.first().id
                                                                        var inglist = mutableListOf<Map<*, *>>()
                                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                                        ingredients?.forEach {
                                                                            inglist.add(mapOf(
                                                                                "ingredientID" to it["IngredientID"],
                                                                                "weight" to it["Weight"]
                                                                            ))
                                                                        }
                                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                                            if(it.exists()){
                                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                                db.collection("Journey").document(journeyId).update(
                                                                                    mapOf(
                                                                                        "totalCalories" to (totalCal+cal),
                                                                                        "lunchMenu" to mapOf(
                                                                                            "calories" to cal,
                                                                                            "ingredients" to inglist,
                                                                                            "menuID" to menuId
                                                                                        )
                                                                                    )
                                                                                ).addOnSuccessListener {
                                                                                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                                                        .whereEqualTo("type", "Dinner").get().addOnSuccessListener {
                                                                                            if(!it.isEmpty){
                                                                                                Log.wtf("inside", it.documents.toString())
                                                                                                var menuId = it.documents.first().id
                                                                                                var inglist = mutableListOf<Map<*, *>>()
                                                                                                var cal = it.documents.first().get("Calories").toString().toInt()
                                                                                                var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                                                                Log.wtf("ada sesuatu", ingredients.toString())
                                                                                                ingredients?.forEach {
                                                                                                    inglist.add(mapOf(
                                                                                                        "ingredientID" to it["IngredientID"],
                                                                                                        "weight" to it["Weight"]
                                                                                                    ))
                                                                                                }
                                                                                                db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                                                                    if(it.exists()){
                                                                                                        var totalCal = it.get("totalCalories").toString().toInt()
                                                                                                        db.collection("Journey").document(journeyId).update(
                                                                                                            mapOf(
                                                                                                                "totalCalories" to (totalCal+cal),
                                                                                                                "dinnerMenu" to mapOf(
                                                                                                                    "calories" to cal,
                                                                                                                    "ingredients" to inglist,
                                                                                                                    "menuID" to menuId
                                                                                                                )
                                                                                                            )
                                                                                                        ).addOnSuccessListener {
                                                                                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                                                                                .whereEqualTo("type", "Snack").get().addOnSuccessListener {
                                                                                                                    if(!it.isEmpty){
                                                                                                                        Log.wtf("inside", it.documents.toString())
                                                                                                                        var menuId = it.documents.first().id
                                                                                                                        var inglist = mutableListOf<Map<*, *>>()
                                                                                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                                                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                                                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                                                                                        ingredients?.forEach {
                                                                                                                            inglist.add(mapOf(
                                                                                                                                "ingredientID" to it["IngredientID"],
                                                                                                                                "weight" to it["Weight"]
                                                                                                                            ))
                                                                                                                        }
                                                                                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                                                                                            if(it.exists()){
                                                                                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                                                                                db.collection("Journey").document(journeyId).update(
                                                                                                                                    mapOf(
                                                                                                                                        "totalCalories" to (totalCal+cal),
                                                                                                                                        "snackMenu" to mapOf(
                                                                                                                                            "calories" to cal,
                                                                                                                                            "ingredients" to inglist,
                                                                                                                                            "menuID" to menuId
                                                                                                                                        )
                                                                                                                                    )
                                                                                                                                )
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }
                                                }

                                            //get lunch menu



                                            //get dinner menu



                                            //get snack menu



                                        }

                                    } else {
                                        var journeyId = it.documents.first().id

                                        //update breakfast menu
                                        if(it.documents.first().get("breakfastMenu") == null){
                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                .whereEqualTo("type", "Breakfast").get().addOnSuccessListener {
                                                    if(!it.isEmpty){
                                                        Log.wtf("inside", it.documents.toString())
                                                        var menuId = it.documents.first().id
                                                        var inglist = mutableListOf<Map<*, *>>()
                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                        ingredients?.forEach {
                                                            inglist.add(mapOf(
                                                                "ingredientID" to it["IngredientID"],
                                                                "weight" to it["Weight"]
                                                            ))
                                                        }
                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                            if(it.exists()){
                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                db.collection("Journey").document(journeyId).update(
                                                                    mapOf(
                                                                        "totalCalories" to (totalCal+cal),
                                                                        "breakfastMenu" to mapOf(
                                                                            "calories" to cal,
                                                                            "ingredients" to inglist,
                                                                            "menuID" to menuId
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                        }

                                        //update lunch menu
                                        if(it.documents.first().get("lunchMenu") == null){

                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                .whereEqualTo("type", "Lunch").get().addOnSuccessListener {
                                                    if(!it.isEmpty){
                                                        Log.wtf("inside", it.documents.toString())
                                                        var menuId = it.documents.first().id
                                                        var inglist = mutableListOf<Map<*, *>>()
                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                        ingredients?.forEach {
                                                            inglist.add(mapOf(
                                                                "ingredientID" to it["IngredientID"],
                                                                "weight" to it["Weight"]
                                                            ))
                                                        }
                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                            if(it.exists()){
                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                db.collection("Journey").document(journeyId).update(
                                                                    mapOf(
                                                                        "totalCalories" to (totalCal+cal),
                                                                        "lunchMenu" to mapOf(
                                                                            "calories" to cal,
                                                                            "ingredients" to inglist,
                                                                            "menuID" to menuId
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                        }

                                        //update dinner menu
                                        if(it.documents.first().get("dinnerMenu") == null){
                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                .whereEqualTo("type", "Dinner").get().addOnSuccessListener {
                                                    if(!it.isEmpty){
                                                        Log.wtf("inside", it.documents.toString())
                                                        var menuId = it.documents.first().id
                                                        var inglist = mutableListOf<Map<*, *>>()
                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                        ingredients?.forEach {
                                                            inglist.add(mapOf(
                                                                "ingredientID" to it["IngredientID"],
                                                                "weight" to it["Weight"]
                                                            ))
                                                        }
                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                            if(it.exists()){
                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                db.collection("Journey").document(journeyId).update(
                                                                    mapOf(
                                                                        "totalCalories" to (totalCal+cal),
                                                                        "dinnerMenu" to mapOf(
                                                                            "calories" to cal,
                                                                            "ingredients" to inglist,
                                                                            "menuID" to menuId
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                        }

                                        //update snack menu
                                        if(it.documents.first().get("snackMenu") == null){
                                            db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("day", res)
                                                .whereEqualTo("type", "Snack").get().addOnSuccessListener {
                                                    if(!it.isEmpty){
                                                        Log.wtf("inside", it.documents.toString())
                                                        var menuId = it.documents.first().id
                                                        var inglist = mutableListOf<Map<*, *>>()
                                                        var cal = it.documents.first().get("Calories").toString().toInt()
                                                        var ingredients = it.documents.first().get("CustomMealIngredients") as List<Map<*, *>>
                                                        Log.wtf("ada sesuatu", ingredients.toString())
                                                        ingredients?.forEach {
                                                            inglist.add(mapOf(
                                                                "ingredientID" to it["IngredientID"],
                                                                "weight" to it["Weight"]
                                                            ))
                                                        }
                                                        db.collection("Journey").document(journeyId).get().addOnSuccessListener {
                                                            if(it.exists()){
                                                                var totalCal = it.get("totalCalories").toString().toInt()
                                                                db.collection("Journey").document(journeyId).update(
                                                                    mapOf(
                                                                        "totalCalories" to (totalCal+cal),
                                                                        "snackMenu" to mapOf(
                                                                            "calories" to cal,
                                                                            "ingredients" to inglist,
                                                                            "menuID" to menuId
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                        }

                                    }
                                }
                        }
                    }
                }
    }
}