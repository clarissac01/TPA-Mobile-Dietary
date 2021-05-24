package edu.bluejack20_2.dietary

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.github.aakira.expandablelayout.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class FriendJourneyAdapter(private val journeyList: MutableList<JourneyItem>?, private val context: Context)
    : RecyclerView.Adapter<FriendJourneyAdapter.FriendJourneyHolder>(){

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendJourneyAdapter.FriendJourneyHolder {
        val journeyView = LayoutInflater.from(parent.context).inflate(R.layout.friend_journey_item, parent, false)

        return FriendJourneyHolder(journeyView, false)
    }

    override fun getItemCount(): Int {
        return journeyList?.size!!
    }

    class FriendJourneyHolder(itemView: View, isExpandable: Boolean): RecyclerView.ViewHolder(itemView){
        var journeyDate: TextView = itemView.findViewById(R.id.journeyDate)
        var journeyCalories: TextView = itemView.findViewById(R.id.journeyCalories)
        var breakfast: TextView = itemView.findViewById(R.id.breakfastDetail)
        var lunch: TextView = itemView.findViewById(R.id.lunchDetail)
        var dinner: TextView = itemView.findViewById(R.id.dinnerDetail)
        var snack: TextView = itemView.findViewById(R.id.snackDetail)

        var expandableLayoutBreakfast: ExpandableLinearLayout = itemView.findViewById(R.id.expandable_layout_breakfast)
        var expandableViewBreakfast: RecyclerView = itemView.findViewById(R.id.expandable_breakfast_view)

        var expandableLayoutLunch: ExpandableLinearLayout = itemView.findViewById(R.id.expandable_layout_lunch)
        var expandableViewLunch: RecyclerView = itemView.findViewById(R.id.expandable_lunch_view)

        var expandableLayoutDinner: ExpandableLinearLayout = itemView.findViewById(R.id.expandable_layout_dinner)
        var expandableViewDinner: RecyclerView = itemView.findViewById(R.id.expandable_dinner_view)

        var expandableLayoutSnack: ExpandableLinearLayout = itemView.findViewById(R.id.expandable_layout_snack)
        var expandableViewSnack: RecyclerView = itemView.findViewById(R.id.expandable_snack_view)
    }

    override fun onBindViewHolder(holder: FriendJourneyHolder, position: Int) {
        val journeyItem = journeyList?.get(position)

        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(journeyItem?.date?.toDate())
        holder.journeyCalories.text = holder.itemView.context.getString(R.string.calories, journeyItem?.calories.toString())
        holder.journeyDate.text = date


        holder.breakfast.setOnClickListener{
            var journeyId = journeyList?.get(position)!!.journeyId
            val ingredientsList = getIngredients(journeyId, "breakfastMenu", holder.expandableViewBreakfast)
                    if(!journeyList.get(position).isBreakfastExpand){
                        holder.expandableViewBreakfast.adapter =
                            NonEditableIngredientAdapter(ingredientsList, context)
                        holder.expandableViewBreakfast.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        holder.expandableLayoutBreakfast.initLayout()
                        journeyList.get(position).isBreakfastExpand = true
                        holder.expandableLayoutBreakfast.expand()
                    }else{
                        journeyList.get(position).isBreakfastExpand = false
                        holder.expandableLayoutBreakfast.collapse()
                        holder.expandableLayoutBreakfast.setInRecyclerView(false)
                        holder.expandableViewBreakfast.adapter = null
                        holder.expandableLayoutBreakfast.initLayout()
                    }
        }

        holder.lunch.setOnClickListener{
            var journeyId = journeyList?.get(position)!!.journeyId
            val ingredientsList = getIngredients(journeyId, "lunchMenu", holder.expandableViewLunch)
                    if(!journeyList.get(position).isLunchExpand){
                        holder.expandableViewLunch.adapter =
                            NonEditableIngredientAdapter(ingredientsList, context)
                        holder.expandableViewLunch.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        holder.expandableLayoutLunch.initLayout()
                        journeyList.get(position).isLunchExpand = true
                        holder.expandableLayoutLunch.expand()
                    }else{
                        journeyList.get(position).isLunchExpand = false
                        holder.expandableLayoutLunch.collapse()
                        holder.expandableLayoutLunch.setInRecyclerView(false)
                        holder.expandableViewLunch.adapter = null
                        holder.expandableLayoutLunch.initLayout()
                    }
        }

        holder.dinner.setOnClickListener{
            var journeyId = journeyList?.get(position)!!.journeyId
            val ingredientsList = getIngredients(journeyId, "dinnerMenu", holder.expandableViewDinner)
                    if(!journeyList.get(position).isDinnerExpand){
                        holder.expandableViewDinner.adapter =
                            NonEditableIngredientAdapter(ingredientsList, context)
                        holder.expandableViewDinner.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        holder.expandableLayoutDinner.initLayout()
                        journeyList.get(position).isDinnerExpand = true
                        holder.expandableLayoutDinner.expand()
                    }else{
                        journeyList.get(position).isDinnerExpand = false
                        holder.expandableLayoutDinner.collapse()
                        holder.expandableLayoutDinner.setInRecyclerView(false)
                        holder.expandableViewDinner.adapter = null
                        holder.expandableLayoutDinner.initLayout()
                    }
        }

        holder.snack.setOnClickListener{
            var journeyId = journeyList?.get(position)!!.journeyId
            val ingredientsList = getIngredients(journeyId, "snackMenu", holder.expandableViewSnack)
                    if(!journeyList.get(position).isSnackExpand){
                        holder.expandableViewSnack.adapter =
                            NonEditableIngredientAdapter(ingredientsList, context)
                        holder.expandableViewSnack.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        holder.expandableLayoutSnack.initLayout()
                        journeyList.get(position).isSnackExpand = true
                        holder.expandableLayoutSnack.expand()
                    }else{
                        journeyList.get(position).isSnackExpand = false
                        holder.expandableLayoutSnack.collapse()
                        holder.expandableLayoutSnack.setInRecyclerView(false)
                        holder.expandableViewSnack.adapter = null
                        holder.expandableLayoutSnack.initLayout()
                    }
        }

    }

    fun getIngredients(journeyId: String, type: String, rv: RecyclerView): MutableList<IngredientItem>?{
        val list = ArrayList<IngredientItem>()

        db.collection("Journey").document(journeyId).get().addOnSuccessListener{
            if(it?.exists()!!){
                var meal = it.data?.get(type) as Map<*, *>
                meal["menuID"]
                var ingredients = meal["ingredients"] as List<Map<*, *>>?
                var weight = 0F
                var name = ""
                ingredients?.forEach {
                    weight = it["weight"].toString().toFloat()
                    val ingredientId = it["ingredientID"].toString()
                    db.collection("MainIngredients").document(it["ingredientID"].toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            name = it.data?.get("IngredientsName")!!.toString()
                            list.add(IngredientItem(ingredientId, name, 0F, weight))
                            rv.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        return list
    }

}
