package edu.bluejack20_2.dietary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DinnerFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var menuName: TextView
    private lateinit var calCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dinner, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuName = view.findViewById(R.id.menuName)
        calCount = view.findViewById(R.id.calCount)

        initMenu()
    }

    fun initMenu(){

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(!it?.isEmpty!!){
                val getMapping = it.documents.first().get("plan") as Map<*, *>
                val menuId = getMapping["dinnerMenu"].toString()
                db.collection("CustomMeals").document(menuId).get().addOnSuccessListener {
                    if(it.exists()){
                        menuName.text = it.getString("CustomMealName")
                        calCount.text = it.get("Calories").toString() + " kcal"
                    }
                }
            }
        }


    }

}