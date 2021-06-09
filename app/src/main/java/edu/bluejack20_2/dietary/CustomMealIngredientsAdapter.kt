package edu.bluejack20_2.dietary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class CustomMealIngredientsAdapter (
    var customMealIngredient: MutableList<CustomMealIngredientData>,
    val ingredient: MutableMap<String, Int>
): RecyclerView.Adapter<CustomMealIngredientsAdapter.CustomMealIngredientsViewHolder>() {
    inner class CustomMealIngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomMealIngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate((R.layout.custom_meal_ingredients), parent, false)

        return CustomMealIngredientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomMealIngredientsViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<MaterialTextView>(R.id.main_ingredients_name).text = customMealIngredient[position].ingredientName
//            findViewById<MaterialTextView>(R.id.main_ingredients_cal).text = resources.getString(
//                R.string.calories,
//                customMealIngredient[position].ingredientCalories.toString()
//            )
        }
    }

    override fun getItemCount(): Int {
        return customMealIngredient.size
    }
}