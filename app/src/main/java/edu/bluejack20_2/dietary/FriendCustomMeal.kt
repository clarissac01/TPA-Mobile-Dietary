package edu.bluejack20_2.dietary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendCustomMeal(var FriendID: String = "") : Fragment() {

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mealList = getList(view)!!
    }

    override fun onResume() {
        super.onResume()

        var mealList = getList(requireView())
    }

    fun getList(view: View) : MutableList<MealItem>?{
        val list = mutableListOf<MealItem>()

        db.collection("users").whereEqualTo("username", FirebaseAuth.getInstance().currentUser.displayName).get().addOnSuccessListener {
            if (!it.isEmpty) {
                var currentuserlist = mutableListOf<String>()
                db.collection("CustomMeals").whereEqualTo("UserID", it.documents.first().id).get().addOnSuccessListener {
                    if(!it.isEmpty) {
                        it.documents.forEach {
                            if(it?.get("OwnerID") != null){
                                if(it.get("OwnerID")?.equals(FriendID)!!) {
                                    currentuserlist.add(it.getString("OwnerMenuID")!!)
                                }
                            }
                        }
                        db.collection("CustomMeals").whereEqualTo("UserID", FriendID).get().addOnSuccessListener {
                            if(!it.isEmpty){
                                it.documents.forEach{
                                    var menuID = it.id
                                    var mealname = it.getString("CustomMealName")
                                    var calories = it.get("Calories").toString().toFloat()
                                    if(currentuserlist.contains(menuID)) {
                                        list.add(MealItem(menuID, mealname!!, calories, true, true))
                                        view.findViewById<RecyclerView>(R.id.friendmealrecyclerview)?.adapter?.notifyDataSetChanged()
                                    }
                                    else{
                                        list.add(MealItem(menuID, mealname!!, calories, true, false))
                                        view.findViewById<RecyclerView>(R.id.friendmealrecyclerview)?.adapter?.notifyDataSetChanged()
                                    }
                                }
                                if(list.size > 0){
                                    view.findViewById<RecyclerView>(R.id.friendmealrecyclerview).adapter = FriendCustomMealAdapter(FriendID, list, view.context)
                                    view.findViewById<RecyclerView>(R.id.friendmealrecyclerview).layoutManager =
                                        GridLayoutManager(view.context, 2)
                                }else{
                                    view.findViewById<TextView>(R.id.nofriendcustommealmessage).visibility = View.VISIBLE
                                }
                            }else{
                                view.findViewById<TextView>(R.id.nofriendcustommealmessage).visibility = View.VISIBLE
                            }
                        }.addOnFailureListener {
                            view.findViewById<TextView>(R.id.nofriendcustommealmessage).visibility = View.VISIBLE
                        }
                    }
                }.addOnFailureListener {

                }

            }
        }


        return list
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_custom_meal, container, false)
    }

}