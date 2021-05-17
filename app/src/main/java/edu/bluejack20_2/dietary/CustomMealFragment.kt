package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CustomMealFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser


        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            val userID = it.documents.first().id
            db.collection("CustomMeals").whereEqualTo("UserID", userID).get().addOnCompleteListener{

                val result: StringBuffer = StringBuffer()

                var customMealList = mutableListOf<CustomMealData>()
                for (document in it.result!!) {
                    if(document.data?.get("isCustom").toString().toBoolean()) {
                        val data = CustomMealData(
                            document.data?.getValue("CustomMealName").toString(),
                            document.data?.getValue("Calories").toString().toFloat(),
                            document.id
                        )
                        customMealList.add(data)
                    }
                }

                var adapter = CustomMealAdapter(customMealList)
                requireActivity().findViewById<RecyclerView>(R.id.rvCustomMeal).adapter = adapter
                requireActivity().findViewById<RecyclerView>(R.id.rvCustomMeal).layoutManager = LinearLayoutManager(context)
            }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.add_custom_meal_btn).setOnClickListener {
            startActivity(Intent(requireContext(), AddCustomMealActivity::class.java))
        }

    }
}