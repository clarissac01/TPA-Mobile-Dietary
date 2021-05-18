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
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt


class FriendCustomMealAdapter(var friendid: String, private val mealList: MutableList<MealItem>?, private val context: Context)
    : RecyclerView.Adapter<FriendCustomMealAdapter.FriendCustomMealHolder>(){

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    lateinit var currentUserID : String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendCustomMealAdapter.FriendCustomMealHolder {
        val mealView = LayoutInflater.from(parent.context).inflate(R.layout.friend_custom_meal_item, parent, false)

        return FriendCustomMealHolder(mealView, false)
    }

    override fun getItemCount(): Int {
        return mealList?.size!!
    }

    class FriendCustomMealHolder(itemView: View, isExpandable: Boolean): RecyclerView.ViewHolder(itemView){
        var friendMenuName: TextView = itemView.findViewById(R.id.friendMenuName)
        var friendMenuCal: TextView = itemView.findViewById(R.id.friendMenuCal)
        var pict: ImageView = itemView.findViewById(R.id.pict)
        var cardView: View = itemView.findViewById(R.id.friend_custom_meal_view)
        var addmealBtn: FloatingActionButton = itemView.findViewById(R.id.addfriendmealbtn)

    }

    override fun onBindViewHolder(holder: FriendCustomMealHolder, position: Int) {
        val mealItem = mealList?.get(position)

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(!it?.isEmpty!!){
                currentUserID = it.documents.first().id
            }
        }

        holder.friendMenuCal.text = mealItem?.mealCalories.toString() + " kcal"
        holder.friendMenuName.text = mealItem?.mealName.toString()

        var listviewB= mutableListOf<Int>()

        if(mealItem?.hasMeal!!){
            holder.addmealBtn.visibility = View.INVISIBLE
        }else{
            holder.addmealBtn.visibility = View.VISIBLE
        }

        for(i in 1..mealList!!.size step 4){
            listviewB.add(i)
            listviewB.add(i+1)
        }

        if(listviewB.contains(position)){
            holder.pict.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_undraw_cooking_lyxy__1_))
        }else{
            holder.pict.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_undraw_ice_cream_s2rh__2_))
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(
                it.context,
                FriendCustomMealDetail(mealItem)::class.java
            )
            intent.putExtra("menuId", mealItem?.mealId)
            intent.putExtra("hasMeal", mealItem.hasMeal)
            intent.putExtra("friendid", friendid)
            it.context.startActivity(intent)
        }

        holder.addmealBtn.setOnClickListener{
            MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure?")
                .setPositiveButton("NO") { dialog, which ->
                    // Respond to negative button press
                }
                .setNegativeButton("YES") { dialog, which ->
                    // Respond to positive button press
                    db.collection("CustomMeals").document(mealItem.mealId).get().addOnSuccessListener {
                        if(it.exists()){
                            db.collection("CustomMeals").add(it.data!!).addOnSuccessListener {
                                Tasks.whenAll(
                                    db.collection("CustomMeals").document(it.id).update("UserID", currentUserID),
                                    db.collection("CustomMeals").document(it.id).update("OwnerID", friendid),
                                    db.collection("CustomMeals").document(it.id).update("OwnerMenuID", mealItem.mealId)

                                ).addOnSuccessListener {
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    holder.addmealBtn.visibility = View.INVISIBLE
                    mealItem.hasMeal = true
                }
                .show()
        }

    }

}
