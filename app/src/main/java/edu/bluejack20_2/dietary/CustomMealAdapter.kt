package edu.bluejack20_2.dietary

import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class CustomMealAdapter(
    var customMealItem: MutableList<CustomMealData>
) : RecyclerView.Adapter<CustomMealAdapter.CustomMealViewHolder>() {
    inner class CustomMealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomMealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_meal, parent, false)

        return CustomMealViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomMealViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<MaterialTextView>(R.id.custom_meal_name).text = customMealItem[position].customMealName
            findViewById<MaterialTextView>(R.id.custom_meal_cal).text = resources.getString(
                R.string.calories,
                customMealItem[position].customMealCalories.toString()
            )

            findViewById<CardView>(R.id.cvCustomMeal).setOnClickListener{
                val intent = Intent(context, CustomMealDetail::class.java)
                intent.putExtra("customMealId", customMealItem[position].customMealId)
                this.context.startActivity(intent)
            }

            findViewById<MaterialButton>(R.id.remove_custom_meal_btn).setOnClickListener {
                val customMealId = customMealItem[position].customMealId
                FirebaseFirestore.getInstance().collection("CustomMeals").document(customMealId).delete().addOnSuccessListener {
                    customMealItem.remove(customMealItem[position])
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Custom Meal Deleted", Toast.LENGTH_SHORT).show()
                }
            }

            findViewById<MaterialButton>(R.id.update_custom_meal_btn).setOnClickListener {
                val customMealEdit = findViewById<EditText>(R.id.custom_meal_edit)
                customMealEdit.hint = customMealItem[position].customMealName
                customMealEdit.visibility = View.VISIBLE
                findViewById<MaterialButton>(R.id.update_submit_btn).visibility = View.VISIBLE
                findViewById<MaterialButton>(R.id.cancel_btn).visibility = View.VISIBLE
                findViewById<MaterialTextView>(R.id.custom_meal_name).visibility = View.INVISIBLE
                findViewById<MaterialTextView>(R.id.custom_meal_cal).visibility = View.INVISIBLE
                findViewById<MaterialButton>(R.id.update_custom_meal_btn).visibility = View.INVISIBLE
                findViewById<MaterialButton>(R.id.remove_custom_meal_btn).visibility = View.INVISIBLE
            }

            findViewById<MaterialButton>(R.id.update_submit_btn).setOnClickListener {
                val newCustomMeal = findViewById<EditText>(R.id.custom_meal_edit).text.toString()
                val customMealName = findViewById<MaterialTextView>(R.id.custom_meal_name)
                val customMealId = customMealItem[position].customMealId

                if(newCustomMeal == "") {
                    Toast.makeText(context, "Custom meal name can't be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else {
                    FirebaseFirestore.getInstance().collection("CustomMeals").document(customMealId).update("CustomMealName", newCustomMeal).addOnSuccessListener {
                        findViewById<EditText>(R.id.custom_meal_edit).visibility = View.INVISIBLE
                        findViewById<MaterialButton>(R.id.update_submit_btn).visibility = View.INVISIBLE
                        findViewById<MaterialButton>(R.id.cancel_btn).visibility = View.INVISIBLE
                        customMealName.visibility = View.VISIBLE
                        findViewById<MaterialTextView>(R.id.custom_meal_cal).visibility = View.VISIBLE
                        findViewById<MaterialButton>(R.id.update_custom_meal_btn).visibility = View.VISIBLE
                        findViewById<MaterialButton>(R.id.remove_custom_meal_btn).visibility = View.VISIBLE
                        Toast.makeText(holder.itemView.context, "Update Custom Meal Success!", Toast.LENGTH_SHORT).show()
                    }
                }


                FirebaseFirestore.getInstance().collection("CustomMeals").document(customMealId).get().addOnSuccessListener {
                    customMealName.text = it.getString("CustomMealName").toString()
                }
            }

            findViewById<MaterialButton>(R.id.cancel_btn).setOnClickListener {
                findViewById<EditText>(R.id.custom_meal_edit).visibility = View.INVISIBLE
                findViewById<MaterialButton>(R.id.update_submit_btn).visibility = View.INVISIBLE
                findViewById<MaterialButton>(R.id.cancel_btn).visibility = View.INVISIBLE
                findViewById<MaterialTextView>(R.id.custom_meal_name).visibility = View.VISIBLE
                findViewById<MaterialTextView>(R.id.custom_meal_cal).visibility = View.VISIBLE
                findViewById<MaterialButton>(R.id.update_custom_meal_btn).visibility = View.VISIBLE
                findViewById<MaterialButton>(R.id.remove_custom_meal_btn).visibility = View.VISIBLE
            }

        }
    }

    override fun getItemCount(): Int {
        return customMealItem.size
    }


}