package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class MainIngredients : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ingredients)

        val db = FirebaseFirestore.getInstance()
        db.collection("MainIngredients").get().addOnSuccessListener {

            val result: StringBuffer = StringBuffer()

            var mainIngredientsList = mutableListOf<MainIngredientsData>()
            for (document in it.documents) {
                    val data = MainIngredientsData(
                        document.data?.getValue("IngredientsName").toString(),
                        document.data?.getValue("IngredientsCalories").toString().toInt(),
                        document.data?.getValue("IngredientsWeight").toString().toInt()
                    )

                mainIngredientsList.add(data)
            }

            Log.wtf("size", mainIngredientsList.size.toString())
            var adapter = MainIngredientsAdapter(mainIngredientsList)
            findViewById<RecyclerView>(R.id.rvMainIngredients).adapter = adapter
            findViewById<RecyclerView>(R.id.rvMainIngredients).layoutManager =
                LinearLayoutManager(this)
        }
    }
}