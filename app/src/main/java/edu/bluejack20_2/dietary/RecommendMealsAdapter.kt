package edu.bluejack20_2.dietary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt


class RecommendMealsAdapter(parentActivity: AppCompatActivity, private val mealType: String, private val mealList: MutableList<MealItem>?, private val context: Context)
    : RecyclerView.Adapter<RecommendMealsAdapter.RecommendMealsHolder>(){

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var parentActivity = parentActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendMealsAdapter.RecommendMealsHolder {
        val recommendMealView = LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)

        return RecommendMealsHolder(recommendMealView, false)
    }

    override fun getItemCount(): Int {
        return mealList?.size!!
    }

    class RecommendMealsHolder(itemView: View, isExpandable: Boolean): RecyclerView.ViewHolder(itemView){
        var mealName: TextView = itemView.findViewById(R.id.chooseMealName)
        var mealCalories: TextView = itemView.findViewById(R.id.chooseMealCalories)
        var showMoreText: TextView = itemView.findViewById(R.id.detailBtn)
        var showMoreBtn: ImageView = itemView.findViewById(R.id.detailArrow)
        val chooseMealBtn: Button = itemView.findViewById(R.id.choosethisMeal)

        var expandableLayout: ExpandableLinearLayout = itemView.findViewById(R.id.expandable_layout)
        var expandableView: RecyclerView = itemView.findViewById(R.id.expandable_view)


    }

    override fun onBindViewHolder(holder: RecommendMealsHolder, position: Int) {
        val mealItem = mealList?.get(position)

        holder.mealName.text = mealItem?.mealName!!
        holder.mealCalories.text = mealItem.mealCalories.toString() + " kcal"

        holder.chooseMealBtn.setOnClickListener {
            changeMeal(it, mealItem)
        }

        holder.showMoreBtn.setOnClickListener{
            var mealId = mealList?.get(position)!!.mealId
            val ingredientsList = getIngredients(mealId, holder.expandableView)
            db.collection("CustomMeals").document(mealId).get().addOnSuccessListener{
                if(it?.exists()!!){
                    holder.expandableView.adapter =
                        NonEditableIngredientAdapter(ingredientsList, context)
                    holder.expandableView.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    holder.expandableView.setHasFixedSize(true)
                    holder.expandableLayout.expand();
                }
            }
        }

        holder.showMoreText.setOnClickListener{
            var mealId = mealList?.get(position)!!.mealId
            val ingredientsList = getIngredients(mealId, holder.expandableView)
            db.collection("CustomMeals").document(mealId).get().addOnSuccessListener{
                if(it?.exists()!!){
                    holder.expandableView.adapter =
                        NonEditableIngredientAdapter(ingredientsList, context)
                    holder.expandableView.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    holder.expandableView.setHasFixedSize(true)
                    holder.expandableLayout.expand();
                }
            }
        }

        holder.expandableLayout.setListener(object : ExpandableLayoutListenerAdapter() {
            override fun onPreOpen() {
            }
            override fun onPreClose() {
                holder.expandableLayout.collapse();
            }
        })

    }

    fun getIngredients(mealId: String, rv: RecyclerView): MutableList<IngredientItem>?{
        val list = ArrayList<IngredientItem>()

        db.collection("CustomMeals").document(mealId).get().addOnSuccessListener{
            if(it?.exists()!!){
                var mealings = it.data?.get("CustomMealIngredients") as List<Map<*, *>>
                var calCount = 0F
                var weight = 0F
                var name = ""
                mealings.forEach {
                    calCount = it["Weight"].toString().toFloat()
                    weight = it["Weight"].toString().toFloat()
                    val ingredientId = it["IngredientID"].toString()
                    db.collection("MainIngredients").document(it["IngredientID"].toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            calCount /= it.data?.get("IngredientsWeight")!!.toString().toFloat()
                            calCount *= it.data?.get("IngredientsCalories")!!.toString().toFloat()
                            calCount = calCount.roundToInt().toFloat()
                            name = it.data?.get("IngredientsName")!!.toString()
                            list.add(IngredientItem(ingredientId, name, calCount, weight))
                            rv.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        return list
    }

    fun changeMeal(view: View, mealItem: MealItem){
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Change "+ mealType +" meal?")
            .setPositiveButton("NO") { dialog, which ->
                // Respond to negative button press
            }
            .setNegativeButton("YES") { dialog, which ->
                // Respond to positive button press
                db.collection("users").whereEqualTo("username", user.displayName).get()
                    .addOnSuccessListener {
                        if(!it?.isEmpty!!){
                            val userid = it.documents.first().id.toString()
                            when(mealType){
                                "Breakfast" -> {
                                    db.collection("users").document(userid).update("plan.breakfastMenu", mealItem.mealId).addOnSuccessListener {
                                        Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                        parentActivity.finish()
                                    }
                                }
                                "Lunch" -> {
                                    db.collection("users").document(userid).update("plan.lunchMenu", mealItem.mealId).addOnSuccessListener {
                                        Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                        parentActivity.finish()
                                    }
                                }
                                "Dinner" -> {
                                    db.collection("users").document(userid).update("plan.dinnerMenu", mealItem.mealId).addOnSuccessListener {
                                        Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                        parentActivity.finish()
                                    }
                                }
                                "Snack" -> {
                                    db.collection("users").document(userid).update("plan.snackMenu", mealItem.mealId).addOnSuccessListener {
                                        Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                        parentActivity.finish()
                                    }
                                }

                            }

                        }
                    }
            }
            .show()
    }

}
