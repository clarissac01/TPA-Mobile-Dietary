package edu.bluejack20_2.dietary

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class JourneyFragment : Fragment(R.layout.fragment_journey) {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    val journeyList = mutableListOf<JourneyData>()
    var lim:Long = 5
    lateinit var adapter: JourneyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getData {
            adapter = JourneyAdapter(journeyList)
            view.findViewById<RecyclerView>(R.id.rvJourney).adapter = adapter
            view.findViewById<RecyclerView>(R.id.rvJourney).layoutManager = LinearLayoutManager(
                context
            )
        }

        requireActivity().findViewById<Button>(R.id.journey_more_btn).setOnClickListener {
            lim = lim + 5
            if(lim >= journeyList.size) {
                Toast.makeText(context, getString(R.string.limit_view_more), Toast.LENGTH_SHORT).show()
            }
            getData {
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun getData(callback: () -> Unit) {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            val userID = it.documents.first().id
            db.collection("CustomMeals").get().addOnSuccessListener {
                db.collection("Journey").whereEqualTo("userID", userID).limit(lim).get().addOnSuccessListener{ journey ->
                    val tempList = mutableListOf<JourneyData>()
                    for(document in journey.documents) {
                        val breakfast = document?.get("breakfastMenu") as Map<String, Any>
                        val lunch = document.get("lunchMenu") as Map<String, Any>
                        val dinner = document.get("dinnerMenu") as Map<String, Any>
                        val snack = document.get("snackMenu") as Map<String, Any>
                        val pattern = "yyyy-MM-dd"
                        val simpleDateFormat = SimpleDateFormat(pattern)
                        val date = simpleDateFormat.format(document.getTimestamp("Date")?.toDate())
                        val perDay = mutableMapOf(
                            "timestamp" to date
                        )
                        perDay["breakfast"] = it.find {
                            it.id == breakfast["menuID"]
                        }?.get("CustomMealName").toString()
                        perDay["lunch"] = it.find {
                            it.id == lunch["menuID"]
                        }?.get("CustomMealName").toString()
                        perDay["dinner"] = it.find {
                            it.id == dinner["menuID"]
                        }?.get("CustomMealName").toString()
                        perDay["snack"] = it.find {
                            it.id == snack["menuID"]
                        }?.get("CustomMealName").toString()
                        Log.wtf("map", perDay.toString())
                        val data = JourneyData(
                            document.get("totalCalories").toString().toInt(),
                            perDay["timestamp"].toString(),
                            perDay["breakfast"].toString(),
                            perDay["lunch"].toString(),
                            perDay["dinner"].toString(),
                            perDay["snack"].toString()
                        )
                        tempList.add(data)
                    }
                    journeyList.clear()
                    journeyList.addAll(tempList.toList())
                    callback()
                }
            }
        }
    }
}