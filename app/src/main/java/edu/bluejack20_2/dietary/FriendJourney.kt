package edu.bluejack20_2.dietary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FriendJourney(var FriendID: String) : Fragment() {

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.wtf("this friend iddd", FriendID)

        var journeyList = getList(view)!!
    }

    fun getList(view: View) : MutableList<JourneyItem>?{
        val list = mutableListOf<JourneyItem>()
        db.collection("Journey").whereEqualTo("userID", FriendID).get().addOnSuccessListener {
            if(!it.isEmpty){
                it.documents.forEach{
                    list.add(JourneyItem(it.id, it.getTimestamp("Date")!!, FriendID, it.get("totalCalories").toString().toInt()))
                    view.findViewById<RecyclerView>(R.id.friendjourneyrecyclerview)?.adapter?.notifyDataSetChanged()
                }
                Log.wtf("wihi id", list.toString())
                if(list.size > 0){
                    view.findViewById<RecyclerView>(R.id.friendjourneyrecyclerview).adapter = FriendJourneyAdapter(list, view.context)
                    view.findViewById<RecyclerView>(R.id.friendjourneyrecyclerview).layoutManager =
                        LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    view.findViewById<RecyclerView>(R.id.friendjourneyrecyclerview).setHasFixedSize(true)
                }else{
                    view.findViewById<TextView>(R.id.nofriendjourneymessage).visibility = View.VISIBLE
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
        return inflater.inflate(R.layout.fragment_friend_journey, container, false)
    }

}