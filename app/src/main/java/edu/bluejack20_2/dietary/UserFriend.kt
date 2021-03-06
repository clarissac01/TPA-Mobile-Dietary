package edu.bluejack20_2.dietary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class UserFriend : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var userfriend_recyclerview: RecyclerView
    private lateinit var username: TextView
    private lateinit var friendlist: List<FriendItem>
    private lateinit var userfriend: FriendItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_friend)
        var user_detail = getIntent().getExtras()?.get("user") as FriendItem?
        userfriend = user_detail!!

        userfriend_recyclerview = findViewById(R.id.users_view)
        username = findViewById(R.id.user_id)
        username.text = userfriend.username
        friendlist = getAllUser()!!
        userfriend_recyclerview.adapter = FriendAdapter(friendlist, this)
        userfriend_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userfriend_recyclerview.setHasFixedSize(true)


    }

    fun back(view: View) {
        this.finish()
    }

    private fun getAllUser(): List<FriendItem>? {
        val list = ArrayList<FriendItem>()
        var docId: String
        var userfriendlist: List<*>? = null
        var user2friendlist: List<*>?

        //get current user friend id
        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener { it ->
            var userfriendlist: List<String>? = null
            if(!it?.isEmpty!!) {
                userfriendlist = it.documents.first().get("friends") as List<String>
            }
                //get friend's friend
                db.collection("users").whereEqualTo("username", userfriend.username).addSnapshotListener() { it, _ ->
                    if(!it?.isEmpty!!){
                        if(it.documents.first().get("friends") != null){
                            var friendlier = it.documents.first().get("friends") as List<String>?

                            for (i in friendlier!!) {
                                var isUserFriend = false
                                //validate if it is current user friend
                                if(userfriendlist?.contains(i)!!){
                                    isUserFriend = true
                                }
                                db.collection("users").document(i).get()
                                    .addOnFailureListener {
                                        Log.wtf("error", it.toString())
                                    }
                                    .addOnSuccessListener {

                                        var res:Int = 0

                                        if(it?.data?.get("plan") != null){
                                            var plan = it?.data?.get("plan") as Map<*, *>
                                            val date1 = Date()
                                            val date2 = plan["startDate"] as Timestamp
                                            var diff = date1.time - date2.toDate().time
                                            res = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                                                .toInt()

                                        }

                                        if (it != null) {
                                            if (it.getString("photoURL") != null) {
                                                var friend = FriendItem(it.getString("username")!!, true, it.getString("photoURL"), res, i.toString(), isUserFriend)
                                                list.add(friend)
                                            } else {
                                                var friend = FriendItem(it.getString("username")!!, false, null, res, i.toString(), isUserFriend)
                                                list.add(friend)
                                            }
                                            userfriend_recyclerview.adapter?.notifyDataSetChanged()
                                        } else {
                                            Log.wtf("ERROR", "errorr")
                                        }
                                    }
                            }

                        }else{
                        }

                    }
                }
        }
        Log.wtf("list", list.toString())
        return list
    }

}