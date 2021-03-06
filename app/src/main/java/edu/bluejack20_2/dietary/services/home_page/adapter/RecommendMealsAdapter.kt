package edu.bluejack20_2.dietary.services.home_page.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class RecommendMealsAdapter(val currday: Int, parentActivity: AppCompatActivity, private val mealType: String, private val mealList: MutableList<MealItem>?, private val context: Context)
    : RecyclerView.Adapter<RecommendMealsAdapter.RecommendMealsHolder>(){

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var parentActivity = parentActivity
    var language = Locale.getDefault().language
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
        holder.mealCalories.text = context.getString(R.string.calories, mealItem.mealCalories.toString() )

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
                            if(language.equals("in")){
                                name = it.data?.get("IngredientsName_in")!!.toString()
                            }else{
                                name = it.data?.get("IngredientsName_en")!!.toString()
                            }
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
        var mealType_in = mealType
        if(language.equals("in")){
            if(mealType.equals("Breakfast")){
                mealType_in = view.context.getString(R.string.breakfast_text)
            }
            if(mealType.equals("Lunch")){
                mealType_in = view.context.getString(R.string.lunch_text)
            }
            if(mealType.equals("Dinner")){
                mealType_in = view.context.getString(R.string.dinner_text)
            }
            else{
                mealType_in = view.context.getString(R.string.snack_text)
            }
        }
        MaterialAlertDialogBuilder(view.context)
            .setTitle(view.context.getString(R.string.change_type_meal, mealType_in))
            .setPositiveButton(view.context.getString(R.string.no)) { dialog, which ->
                // Respond to negative button press
            }
            .setNegativeButton(view.context.getString(R.string.yes)) { dialog, which ->
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
                                                    if(it.get("isCustom")==null){
                                                        Log.wtf("in here", "hellow")
                                                        changeday = it.get("day").toString().toInt()
                                                        db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }else{
                                                        Log.wtf("in there", "hola")
                                                        db.collection("CustomMeals").document(changeid).update("day", 31)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("type", "Breakfast")
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }
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
                                                    if(it.get("isCustom")==null){
                                                        Log.wtf("in here", "hellow")
                                                        changeday = it.get("day").toString().toInt()
                                                        db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }else{
                                                        Log.wtf("in there", "hola")
                                                        db.collection("CustomMeals").document(changeid).update("day", 31)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("type", "Lunch")
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }
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
                                                    if(it.get("isCustom")==null){
                                                        Log.wtf("in here", "hellow")
                                                        changeday = it.get("day").toString().toInt()
                                                        db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }else{
                                                        Log.wtf("in there", "hola")
                                                        db.collection("CustomMeals").document(changeid).update("day", 31)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("type", "Dinner")
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }
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
                                                    if(it.get("isCustom")==null){
                                                        Log.wtf("in here", "hellow")
                                                        changeday = it.get("day").toString().toInt()
                                                        db.collection("CustomMeals").document(changeid).update("day", changeday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
                                                        parentActivity.finish()
                                                    }else{
                                                        Log.wtf("in there", "hola")
                                                        db.collection("CustomMeals").document(changeid).update("day", 31)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("day", currday)
                                                        db.collection("CustomMeals").document(mealItem.mealId).update("type", "Snack")
                                                        Toast.makeText(view.context, view.context.getString(R.string.success_update_meal_type), Toast.LENGTH_LONG)
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
            }
            .show()
    }

}
