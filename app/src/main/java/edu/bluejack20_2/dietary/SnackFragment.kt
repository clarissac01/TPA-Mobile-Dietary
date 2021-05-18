package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SnackFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var menuName: TextView
    private lateinit var calCount: TextView
    private lateinit var menuId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_snack, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuName = view.findViewById(R.id.menuName)
        calCount = view.findViewById(R.id.calCount)

        initMenu()
        view.findViewById<ConstraintLayout>(R.id.snackLayout).setOnClickListener{
            val intent = Intent(
                context,
                MealDetail::class.java
            )
            intent.putExtra("menuId", menuId)
            context?.startActivity(
                intent
            )
        }

        view.findViewById<Button>(R.id.changeSnack).setOnClickListener {
            val intent = Intent(
                context,
                Meal::class.java
            )
            intent.putExtra("type", "Snack")
            context?.startActivity(
                intent
            )
        }
    }

    fun initMenu(){

        db.collection("users").whereEqualTo("username", user.displayName).addSnapshotListener() { it, _ ->
            if(!it?.isEmpty!!){
                val getMapping = it.documents.first().get("plan") as Map<*, *>
                    menuId = getMapping["snackMenu"].toString()
                db.collection("CustomMeals").document(menuId).addSnapshotListener() { it, _ ->
                    if(it?.exists()!!){
                        menuName.text = it.getString("CustomMealName")
                        calCount.text = it.get("Calories").toString() + " kcal"
                    }
                }
            }
        }


    }
}