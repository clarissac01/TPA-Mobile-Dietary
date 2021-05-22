package edu.bluejack20_2.dietary

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.roundToInt

class NonEditableIngredientAdapter(private val ingredientList: MutableList<IngredientItem>?, private val context: Context)
    : RecyclerView.Adapter<NonEditableIngredientAdapter.NonEditableIngredientHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NonEditableIngredientAdapter.NonEditableIngredientHolder {
        val ingredientView = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item_non_editable, parent, false)

        return NonEditableIngredientHolder(ingredientView)
    }

    override fun getItemCount(): Int {
        return ingredientList?.size!!
    }

    class NonEditableIngredientHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val non_ingredientName: TextView = itemView.findViewById(R.id.non_ingredientName)
        val non_ingredientWeight: TextView = itemView.findViewById(R.id.non_ingredientWeight)
    }

    override fun onBindViewHolder(holder: NonEditableIngredientHolder, position: Int) {
        Log.wtf("the list", ingredientList.toString())
        val ingredientItem = ingredientList?.get(position)
        holder.non_ingredientName.text = ingredientItem?.ingredientName!!
        holder.non_ingredientWeight.setText(ingredientItem.weight.roundToInt().toString() + " g")

    }


}
