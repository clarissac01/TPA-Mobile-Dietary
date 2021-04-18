package edu.bluejack20_2.dietary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomMealFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomMealFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        db.collection("CustomMeals").get().addOnCompleteListener{

            val result: StringBuffer = StringBuffer()

            if(it.isSuccessful) {
                for(document in it.result!!) {
                    result.append(document.data.getValue("CustomMealName")).append("\n\n")
                }
                requireActivity().findViewById<TextView>(R.id.custom_meal_fragment).setText(result)
            }
        }
    }
}