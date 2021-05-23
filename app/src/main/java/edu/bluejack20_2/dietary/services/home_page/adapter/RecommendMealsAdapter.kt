package edu.bluejack20_2.dietary.services.home_page.adapter

import android.animation.ObjectAnimator
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
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.github.aakira.expandablelayout.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.IngredientItem
import edu.bluejack20_2.dietary.MealItem
import edu.bluejack20_2.dietary.NonEditableIngredientAdapter
import edu.bluejack20_2.dietary.R
import kotlin.math.roundToInt


class RecommendMealsAdapter(val currday: Int, parentActivity: AppCompatActivity, private val mealType: String, private val mealList: MutableList<MealItem>?, private val context: Context)
    : RecyclerView.Adapter<RecommendMealsAdapter.RecommendMealsHolder>(){

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var parentActivity = parentActivity
//    var expandState = SparseBooleanArray()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendMealsHolder {
        val recommendMealView = LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)

        return RecommendMealsHolder(
            recommendMealView,
            false
        )
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

        holder.showMoreText.setOnClickListener{
            var mealId = mealList?.get(position)!!.mealId
            val ingredientsList = getIngredients(mealId, holder.expandableView)
            db.collection("CustomMeals").document(mealId).get().addOnSuccessListener{
                if(it?.exists()!!){
                    if(!mealList.get(position).isExpand){
                        holder.expandableView.adapter =
                            NonEditableIngredientAdapter(
                                ingredientsList,
                                context
                            )
                        holder.expandableView.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        holder.expandableLayout.initLayout()
                        mealList.get(position).isExpand = true
                        holder.expandableLayout.expand()
                    }else{
                        mealList.get(position).isExpand = false
                        holder.expandableLayout.collapse()
                        holder.expandableLayout.setInRecyclerView(false)
                        holder.expandableView.adapter = null
                    }
                }
            }
        }
    }

    private fun changeRotate(showMoreBtn: ImageView, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(showMoreBtn, "rotation", from, to)
        animator.duration = 300
        animator.interpolator = Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)

        return animator
    }

    fun getIngredients(mealId: String, rv: RecyclerView): MutableList<IngredientItem>?{
        val list = ArrayList<IngredientItem>()

        db.collection("CustomMeals").document(mealId).get().addOnSuccessListener{
            if(it?.exists()!!){
                var mealings = it.data?.get("CustomMealIngredients") as List<Map<*, *>>
                mealings.forEach {
                    var calCount = 0F
                    var weight = 0F
                    var name = ""
                    calCount = it["Weight"].toString().toFloat()
                    weight = it["Weight"].toString().toFloat()
                    val ingredientId = it["IngredientID"].toString()
                    db.collection("MainIngredients").document(it["IngredientID"].toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            calCount /= it.data?.get("IngredientsWeight")!!.toString().toFloat()
                            calCount *= it.data?.get("IngredientsCalories")!!.toString().toFloat()
                            calCount = calCount.roundToInt().toFloat()
                            name = it.data?.get("IngredientsName")!!.toString()
                            list.add(
                                IngredientItem(
                                    ingredientId,
                                    name,
                                    calCount,
                                    weight
                                )
                            )
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
                                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("type", "Breakfast").whereEqualTo("day", currday).get().addOnSuccessListener {
                                        if(!it.isEmpty){
                                            var changeid = it.documents.first().id
                                            var changeday = 0
                                            db.collection("CustomMeals").document(mealItem.mealId).get().addOnSuccessListener {
                                                if(it.exists()){
                                                    changeday = it.get("day").toString().toInt()
                                                    db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                    db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                    Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                                    parentActivity.finish()
                                                }
                                            }
                                        }
                                    }
                                }
                                "Lunch" -> {
                                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("type", "Lunch").whereEqualTo("day", currday).get().addOnSuccessListener {
                                        if(!it.isEmpty){
                                            var changeid = it.documents.first().id
                                            var changeday = 0
                                            db.collection("CustomMeals").document(mealItem.mealId).get().addOnSuccessListener {
                                                if(it.exists()){
                                                    changeday = it.get("day").toString().toInt()
                                                    db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                    db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                    Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                                    parentActivity.finish()
                                                }
                                            }
                                        }
                                    }
                                }
                                "Dinner" -> {
                                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("type", "Dinner").whereEqualTo("day", currday).get().addOnSuccessListener {
                                        if(!it.isEmpty){
                                            var changeid = it.documents.first().id
                                            var changeday = 0
                                            db.collection("CustomMeals").document(mealItem.mealId).get().addOnSuccessListener {
                                                if(it.exists()){
                                                    changeday = it.get("day").toString().toInt()
                                                    db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                    db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                    Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                                    parentActivity.finish()
                                                }
                                            }
                                        }
                                    }
                                }
                                "Snack" -> {
                                    db.collection("CustomMeals").whereEqualTo("UserID", userid).whereEqualTo("type", "Snack").whereEqualTo("day", currday).get().addOnSuccessListener {
                                        if(!it.isEmpty){
                                            var changeid = it.documents.first().id
                                            var changeday = 0
                                            db.collection("CustomMeals").document(mealItem.mealId).get().addOnSuccessListener {
                                                if(it.exists()){
                                                    changeday = it.get("day").toString().toInt()
                                                    db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                    db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                    Toast.makeText(view.context, "Update " + mealType + " meal success!", Toast.LENGTH_LONG)
                                                    parentActivity.finish()
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
            }
            .show()
    }

}
