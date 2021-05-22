package edu.bluejack20_2.dietary

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.roundToInt

class IngredientAdapter(private val mealCalories: TextView,private val ingredientList: MutableList<IngredientItem>?, private val context: Context)
    : RecyclerView.Adapter<IngredientAdapter.IngredientHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientAdapter.IngredientHolder {
            val ingredientView = LayoutInflater.from(parent.context).inflate(R.layout.ingredients_item, parent, false)

            return IngredientHolder(ingredientView)
    }

    override fun getItemCount(): Int {
        return ingredientList?.size!!
    }

    override fun onBindViewHolder(holder: IngredientAdapter.IngredientHolder, position: Int) {
        val ingredientItem = ingredientList?.get(position)
        holder.ingredientName.text = ingredientItem?.ingredientName!!
        holder.ingredientCal.text = ingredientItem.calory.roundToInt().toString() + " kcal"
        holder.ingredientWeight.setText(ingredientItem.weight.roundToInt().toString())
        holder.addBtn.setOnClickListener {
            ingredientList?.get(position)!!.weight  += 50;
            ingredientList?.get(position)!!.calory *= ((ingredientList?.get(position)!!.weight)/(ingredientList.get(position).weight - 50))
            holder.ingredientCal.text = ingredientList?.get(position)!!.calory.roundToInt().toString() + " kcal"
            holder.ingredientWeight.setText(ingredientList.get(position).weight.roundToInt().toString())
            countTotalCal()
        }

        holder.substractBtn.setOnClickListener {
            if(ingredientItem.weight > 50){
                ingredientList?.get(position)!!.weight  -= 50;
                ingredientList?.get(position)!!.calory *= ((ingredientList?.get(position)!!.weight)/(ingredientList.get(position).weight + 50))

                holder.ingredientCal.text = ingredientList.get(position).calory.roundToInt().toString().toInt().toString() + " kcal"
                holder.ingredientWeight.setText(ingredientList.get(position).weight.roundToInt().toString())
                countTotalCal()
            }
        }

        holder.ingredientWeight.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(holder.ingredientWeight.text.toString().toFloat() > 0){
                    ingredientList?.get(position)!!.calory *= ((holder.ingredientWeight.text.toString().toFloat())/(ingredientList?.get(position)!!.weight))
                    ingredientList.get(position).weight  = holder.ingredientWeight.text.toString().toFloat();
                    holder.ingredientCal.text = ingredientList?.get(position)!!.calory.roundToInt().toString().toInt().toString() + " kcal"
                    countTotalCal()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    class IngredientHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        val ingredientCal: TextView = itemView.findViewById(R.id.ingredientCal)
        val ingredientWeight: TextInputEditText = itemView.findViewById(R.id.ingredientWeight)
        val addBtn: Button = itemView.findViewById(R.id.addwBtn)
        val substractBtn: Button = itemView.findViewById(R.id.substractBtn)
    }

    fun countTotalCal(){
        var totalCal : Float = 0F
        ingredientList?.forEach {
            totalCal += it.calory
        }
        mealCalories.text = totalCal.roundToInt().toString() + " kcal"
    }

}
