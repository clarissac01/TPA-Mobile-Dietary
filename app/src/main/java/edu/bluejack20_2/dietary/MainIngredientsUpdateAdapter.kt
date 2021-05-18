package edu.bluejack20_2.dietary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class MainIngredientsUpdateAdapter (
    var mainIngredientsItem: List<MainIngredientsData>,
    val ingredient: MutableMap<String, Int>
) : RecyclerView.Adapter<MainIngredientsUpdateAdapter.MainIngredientsUpdateViewHolder>() {
    inner class MainIngredientsUpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainIngredientsUpdateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.main_ingredients_update, parent, false)

        return MainIngredientsUpdateViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainIngredientsUpdateViewHolder, position: Int) {
        holder.itemView.apply {
            val gram = findViewById<TextInputEditText>(R.id.gram_amount)

            if (ingredient.containsKey(mainIngredientsItem[position].ingredientsId)) {
                gram.setText(ingredient[mainIngredientsItem[position].ingredientsId].toString())
            } else {
                gram.setText("0")
            }
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

            findViewById<Button>(R.id.substractBtn).setOnClickListener {
                val newQty = gram.text.toString().toInt() - 1
                if (gram.text.toString().toInt() > 0) {
                    gram.setText(newQty.toString())
                    updateIngredient(mainIngredientsItem[position], newQty)
                }
            }

            findViewById<Button>(R.id.addwBtn).setOnClickListener {
                val newQty = gram.text.toString().toInt() + 1
                gram.setText(newQty.toString())
                updateIngredient(mainIngredientsItem[position], newQty)
            }
        }
    }

    fun updateIngredient(mainIngredientsData: MainIngredientsData, newQty: Int) {
        if (newQty == 0 && ingredient.containsKey(mainIngredientsData.ingredientsId)) {
            ingredient.remove(mainIngredientsData.ingredientsId)
        } else {
            ingredient[mainIngredientsData.ingredientsId] = newQty
        }
    }

    override fun getItemCount(): Int {
        return mainIngredientsItem.size
    }
}