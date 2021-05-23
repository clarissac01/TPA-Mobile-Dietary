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
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
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
                                        //add breakfast menu
                                        var menucal1 = 0
                                        val customIngredient1 = mutableListOf<Map<*, *>>()
                                        var menucal2 = 0
                                        val customIngredient2 = mutableListOf<Map<*, *>>()
                                        var menucal3 = 0
                                        var menucal4 = 0
                                        val customIngredient3 = mutableListOf<Map<*, *>>()
                                        val customIngredient4 = mutableListOf<Map<*, *>>()
                                        var breakfastmenuid = ""
                                        var lunchmenuid = ""
                                        var dinnermenuid = ""
                                        var snackmenuid = ""
                                        Tasks.whenAll(
                                            db.collection("CustomMeals")
                                                .whereEqualTo("UserID", userid)
                                                .whereEqualTo("day", res)
                                                .whereEqualTo("type", "Breakfast")
                                                .get().addOnSuccessListener {
                                                    if (!it.isEmpty) {
                                                        breakfastmenuid = it.documents.first().id
                                                        db.collection("CustomMeals")
                                                            .document(it.documents.first().id).get()
                                                            .addOnSuccessListener {
                                                                if (it.exists()) {
                                                                    menucal1 =
                                                                        it.get("Calories")
                                                                            .toString()
                                                                            .toFloat().toInt()
                                                                    var inglist =
                                                                        it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                    inglist.forEach {
                                                                        customIngredient1.add(
                                                                            mapOf(
                                                                                "ingredientID" to it["IngredientID"],
                                                                                "weight" to it["Weight"]
                                                                            )
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                    }
                                                    db.collection("CustomMeals")
                                                        .whereEqualTo("UserID", userid)
                                                        .whereEqualTo("day", res)
                                                        .whereEqualTo("type", "Lunch")
                                                        .get().addOnSuccessListener {
                                                            if (!it.isEmpty) {
                                                                lunchmenuid =
                                                                    it.documents.first().id
                                                                db.collection("CustomMeals")
                                                                    .document(it.documents.first().id)
                                                                    .get().addOnSuccessListener {
                                                                        if (it.exists()) {
                                                                            menucal2 =
                                                                                it.get("Calories")
                                                                                    .toString()
                                                                                    .toFloat()
                                                                                    .toInt()
                                                                            var inglist =
                                                                                it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                            inglist.forEach {
                                                                                customIngredient2.add(
                                                                                    mapOf(
                                                                                        "ingredientID" to it["IngredientID"],
                                                                                        "weight" to it["Weight"]
                                                                                    )
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                db.collection("CustomMeals")
                                                                    .whereEqualTo("UserID", userid)
                                                                    .whereEqualTo("day", res)
                                                                    .whereEqualTo("type", "Dinner")
                                                                    .get().addOnSuccessListener {
                                                                        if (!it.isEmpty) {
                                                                            dinnermenuid =
                                                                                it.documents.first().id
                                                                            db.collection("CustomMeals")
                                                                                .document(it.documents.first().id)
                                                                                .get()
                                                                                .addOnSuccessListener {
                                                                                    if (it.exists()) {
                                                                                        menucal3 =
                                                                                            it.get("Calories")
                                                                                                .toString()
                                                                                                .toFloat()
                                                                                                .toInt()
                                                                                        var inglist =
                                                                                            it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                                        inglist.forEach {
                                                                                            customIngredient3.add(
                                                                                                mapOf(
                                                                                                    "ingredientID" to it["IngredientID"],
                                                                                                    "weight" to it["Weight"]
                                                                                                )
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            db.collection("CustomMeals")
                                                                                .whereEqualTo(
                                                                                    "UserID",
                                                                                    userid
                                                                                ).whereEqualTo(
                                                                                    "day",
                                                                                    res
                                                                                ).whereEqualTo(
                                                                                    "type",
                                                                                    "Snack"
                                                                                )
                                                                                .get()
                                                                                .addOnSuccessListener {
                                                                                    if (!it.isEmpty) {
                                                                                        snackmenuid =
                                                                                            it.documents.first().id
                                                                                        db.collection(
                                                                                            "CustomMeals"
                                                                                        )
                                                                                            .document(
                                                                                                it.documents.first().id
                                                                                            )
                                                                                            .get()
                                                                                            .addOnSuccessListener {
                                                                                                if (it.exists()) {
                                                                                                    menucal4 =
                                                                                                        it.get(
                                                                                                            "Calories"
                                                                                                        )
                                                                                                            .toString()
                                                                                                            .toFloat()
                                                                                                            .toInt()
                                                                                                    var inglist =
                                                                                                        it.get(
                                                                                                            "CustomMealIngredients"
                                                                                                        ) as List<Map<*, *>>
                                                                                                    inglist.forEach {
                                                                                                        customIngredient4.add(
                                                                                                            mapOf(
                                                                                                                "ingredientID" to it["IngredientID"],
                                                                                                                "weight" to it["Weight"]
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

                                        ).addOnSuccessListener {
                                            db.collection("Journey").add(
                                                hashMapOf(
                                                    "userID" to userid,
                                                    "totalCalories" to (menucal1 + menucal2 + menucal3 + menucal4),
                                                    "day" to res,
                                                    "Date" to Timestamp.now(),
                                                    "breakfastMenu" to hashMapOf(
                                                        "calories" to menucal1,
                                                        "ingredients" to customIngredient1,
                                                        "menuID" to breakfastmenuid
                                                    ),
                                                    "lunchMenu" to hashMapOf(
                                                        "calories" to menucal2,
                                                        "ingredients" to customIngredient2,
                                                        "menuID" to lunchmenuid
                                                    ),
                                                    "dinnerMenu" to hashMapOf(
                                                        "calories" to menucal3,
                                                        "ingredients" to customIngredient3,
                                                        "menuID" to dinnermenuid
                                                    ),
                                                    "snackMenu" to hashMapOf(
                                                        "calories" to menucal4,
                                                        "ingredients" to customIngredient4,
                                                        "menuID" to snackmenuid
                                                    )
                                                )
                                            ).addOnSuccessListener {
                                                Log.wtf("horray", "finally done")
                                            }

                                        }

                                    } else {
                                        if (it.documents.first().get("breakfastMenu") == null) {
                                            var menucal = 0
                                            var menuId = ""
                                            val customIngredient = mutableListOf<Map<*, *>>()
                                            Tasks.whenAll(
                                                db.collection("CustomMeals")
                                                    .whereEqualTo("UserID", userid)
                                                    .whereEqualTo("day", res)
                                                    .whereEqualTo("type", "Breakfast").get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            menuId = it.documents.first().id
                                                            db.collection("CustomMeals")
                                                                .document(menuId)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        menucal = it.get("Calories")
                                                                            .toString()
                                                                            .toFloat().toInt()
                                                                        var inglist =
                                                                            it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                        inglist.forEach {
                                                                            customIngredient.add(
                                                                                mapOf(
                                                                                    "ingredientID" to it["IngredientID"],
                                                                                    "weight" to it["Weight"]
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }

                                            ).addOnSuccessListener {
                                                db.collection("Journey")
                                                    .whereEqualTo("userID", userid)
                                                    .whereEqualTo("day", res).get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            var journeyid = it.documents.first().id
                                                            db.collection("Journey")
                                                                .document(journeyid)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        var totalCal = 0
                                                                        if (it.get("totalCalories") != null) {
                                                                            totalCal =
                                                                                menucal + it.get("totalCalories")
                                                                                    .toString()
                                                                                    .toInt()
                                                                        } else {
                                                                            totalCal = menucal
                                                                        }
                                                                        db.collection("Journey")
                                                                            .document(journeyid)
                                                                            .update(
                                                                                "breakfastMenu",
                                                                                hashMapOf(
                                                                                    "calories" to menucal,
                                                                                    "ingredients" to customIngredient,
                                                                                    "menuID" to menuId
                                                                                )
                                                                            ).addOnSuccessListener {
                                                                                db.collection("Journey")
                                                                                    .document(
                                                                                        journeyid
                                                                                    ).update(
                                                                                        "totalCalories",
                                                                                        totalCal
                                                                                    )
                                                                            }
                                                                    }

                                                                }
                                                        }
                                                    }
                                            }
                                        }

                                        //lunch Menu

                                        if (it.documents.first().get("lunchMenu") == null) {
                                            var menucal = 0
                                            var menuId = ""
                                            val customIngredient = mutableListOf<Map<*, *>>()
                                            Tasks.whenAll(
                                                db.collection("CustomMeals")
                                                    .whereEqualTo("UserID", userid)
                                                    .whereEqualTo("day", res)
                                                    .whereEqualTo("type", "Lunch").get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            menuId = it.documents.first().id
                                                            db.collection("CustomMeals")
                                                                .document(menuId)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        menucal = it.get("Calories")
                                                                            .toString()
                                                                            .toFloat().toInt()
                                                                        var inglist =
                                                                            it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                        inglist.forEach {
                                                                            customIngredient.add(
                                                                                mapOf(
                                                                                    "ingredientID" to it["IngredientID"],
                                                                                    "weight" to it["Weight"]
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }

                                            ).addOnSuccessListener {
                                                db.collection("Journey")
                                                    .whereEqualTo("userID", userid)
                                                    .whereEqualTo("day", res).get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            var journeyid = it.documents.first().id
                                                            db.collection("Journey")
                                                                .document(journeyid)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        var totalCal = 0
                                                                        if (it.get("totalCalories") != null) {
                                                                            totalCal =
                                                                                menucal + it.get("totalCalories")
                                                                                    .toString()
                                                                                    .toInt()
                                                                        } else {
                                                                            totalCal = menucal
                                                                        }
                                                                        db.collection("Journey")
                                                                            .document(journeyid)
                                                                            .update(
                                                                                "lunchMenu",
                                                                                hashMapOf(
                                                                                    "calories" to menucal,
                                                                                    "ingredients" to customIngredient,
                                                                                    "menuID" to menuId
                                                                                )
                                                                            ).addOnSuccessListener {
                                                                                db.collection("Journey")
                                                                                    .document(
                                                                                        journeyid
                                                                                    ).update(
                                                                                        "totalCalories",
                                                                                        totalCal
                                                                                    )
                                                                            }
                                                                    }

                                                                }
                                                        }
                                                    }
                                            }
                                        }

                                        //dinner Menu

                                        if (it.documents.first().get("dinnerMenu") == null) {
                                            var menucal = 0
                                            var menuId = ""
                                            val customIngredient = mutableListOf<Map<*, *>>()
                                            Tasks.whenAll(
                                                db.collection("CustomMeals")
                                                    .whereEqualTo("UserID", userid)
                                                    .whereEqualTo("day", res)
                                                    .whereEqualTo("type", "Dinner").get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            menuId = it.documents.first().id
                                                            db.collection("CustomMeals")
                                                                .document(menuId)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        menucal = it.get("Calories")
                                                                            .toString()
                                                                            .toFloat().toInt()
                                                                        var inglist =
                                                                            it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                        inglist.forEach {
                                                                            customIngredient.add(
                                                                                mapOf(
                                                                                    "ingredientID" to it["IngredientID"],
                                                                                    "weight" to it["Weight"]
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }

                                            ).addOnSuccessListener {
                                                db.collection("Journey")
                                                    .whereEqualTo("userID", userid)
                                                    .whereEqualTo("day", res).get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            var journeyid = it.documents.first().id
                                                            db.collection("Journey")
                                                                .document(journeyid)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        var totalCal = 0
                                                                        if (it.get("totalCalories") != null) {
                                                                            totalCal =
                                                                                menucal + it.get("totalCalories")
                                                                                    .toString()
                                                                                    .toInt()
                                                                        } else {
                                                                            totalCal = menucal
                                                                        }
                                                                        db.collection("Journey")
                                                                            .document(journeyid)
                                                                            .update(
                                                                                "dinnerMenu",
                                                                                hashMapOf(
                                                                                    "calories" to menucal,
                                                                                    "ingredients" to customIngredient,
                                                                                    "menuID" to menuId
                                                                                )
                                                                            ).addOnSuccessListener {
                                                                                db.collection("Journey")
                                                                                    .document(
                                                                                        journeyid
                                                                                    ).update(
                                                                                        "totalCalories",
                                                                                        totalCal
                                                                                    )
                                                                            }
                                                                    }

                                                                }
                                                        }
                                                    }
                                            }
                                        }

                                        //snack Menu

                                        if (it.documents.first().get("snackMenu") == null) {
                                            var menucal = 0
                                            var menuId = ""
                                            val customIngredient = mutableListOf<Map<*, *>>()
                                            Tasks.whenAll(
                                                db.collection("CustomMeals")
                                                    .whereEqualTo("UserID", userid)
                                                    .whereEqualTo("day", res)
                                                    .whereEqualTo("type", "Snack").get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            menuId = it.documents.first().id
                                                            db.collection("CustomMeals")
                                                                .document(menuId)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        menucal = it.get("Calories")
                                                                            .toString()
                                                                            .toFloat().toInt()
                                                                        var inglist =
                                                                            it.get("CustomMealIngredients") as List<Map<*, *>>
                                                                        inglist.forEach {
                                                                            customIngredient.add(
                                                                                mapOf(
                                                                                    "ingredientID" to it["IngredientID"],
                                                                                    "weight" to it["Weight"]
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }

                                            ).addOnSuccessListener {
                                                db.collection("Journey")
                                                    .whereEqualTo("userID", userid)
                                                    .whereEqualTo("day", res).get()
                                                    .addOnSuccessListener {
                                                        if (!it.isEmpty) {
                                                            var journeyid = it.documents.first().id
                                                            db.collection("Journey")
                                                                .document(journeyid)
                                                                .get().addOnSuccessListener {
                                                                    if (it.exists()) {
                                                                        var totalCal = 0
                                                                        if (it.get("totalCalories") != null) {
                                                                            totalCal =
                                                                                menucal + it.get("totalCalories")
                                                                                    .toString()
                                                                                    .toInt()
                                                                        } else {
                                                                            totalCal = menucal
                                                                        }
                                                                        db.collection("Journey")
                                                                            .document(journeyid)
                                                                            .update(
                                                                                "snackMenu",
                                                                                hashMapOf(
                                                                                    "calories" to menucal,
                                                                                    "ingredients" to customIngredient,
                                                                                    "menuID" to menuId
                                                                                )
                                                                            ).addOnSuccessListener {
                                                                                db.collection("Journey")
                                                                                    .document(
                                                                                        journeyid
                                                                                    ).update(
                                                                                        "totalCalories",
                                                                                        totalCal
                                                                                    )
                                                                            }
                                                                    }

                                                                }
                                                        }
                                                    }
                                            }
                                        }
                                        Log.wtf("done this notif", "hoorayyy")
                                    }
                                }
                        }
                    }
                }
        }
    }
}