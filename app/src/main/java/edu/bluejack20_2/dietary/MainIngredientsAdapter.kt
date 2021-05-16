package edu.bluejack20_2.dietary

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class MainIngredientsAdapter(
    var mainIngredientsItem: List<MainIngredientsData>
) : RecyclerView.Adapter<MainIngredientsAdapter.MainIngredientsViewHolder>() {
    inner class MainIngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainIngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.main_ingredients, parent, false)

        return MainIngredientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainIngredientsViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<MaterialTextView>(R.id.main_ingredients_name).text =
                mainIngredientsItem[position].ingredientsName

            findViewById<MaterialTextView>(R.id.main_ingredients_cal).text = resources.getString(
                    R.string.calories,
                    mainIngredientsItem[position].ingredientsCalories.toString()
                )
            findViewById<MaterialTextView>(R.id.main_ingredients_weight).text =
                resources.getString(
                    R.string.weight,
                    mainIngredientsItem[position].ingredientsWeight.toString()
                )
        }
    }

    override fun getItemCount(): Int {
        return mainIngredientsItem.size
    }
}