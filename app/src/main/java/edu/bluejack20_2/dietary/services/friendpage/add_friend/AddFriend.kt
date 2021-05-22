package edu.bluejack20_2.dietary.services.friendpage.add_friend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.malinskiy.superrecyclerview.SuperRecyclerView
import edu.bluejack20_2.dietary.FriendAdapter
import edu.bluejack20_2.dietary.FriendItem
import edu.bluejack20_2.dietary.R
import java.util.*
import kotlin.collections.ArrayList

class   AddFriend : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    lateinit var search:SearchView
    var usernames: MutableList<String> = mutableListOf()
    lateinit var friendlist: List<FriendItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        friendlist = getAllUser()!!


        search = findViewById<SearchView>(R.id.search_friend)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                    var filter = friendlist?.filter {
                        it.username.contains(query.toString())
                    }
                    if(filter.isEmpty()){
                        Toast.makeText(applicationContext, "User not found!", Toast.LENGTH_LONG).show()
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter = null
                    }else{
                        var alist = friendlist?.toMutableList()
                        alist.clear()
                        alist.addAll(filter)
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter =
                            FriendAdapter(
                                alist,
                                this@AddFriend
                            )
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()

                    }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                search.clearFocus()
                if(newText.equals("")){
                    findViewById<SuperRecyclerView>(R.id.user_view).adapter =
                        FriendAdapter(
                            friendlist,
                            this@AddFriend
                        )
                    findViewById<SuperRecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
                }else{
                    var filter = friendlist?.filter {
                        it.username.contains(newText.toString())
                    }
                    Log.wtf("filter", filter.toString())
                    if(!filter.isEmpty()){
                        var alist = friendlist?.toMutableList()
                        alist.clear()
                        alist.addAll(filter)
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter =
                            FriendAdapter(
                                alist,
                                this@AddFriend
                            )
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
                    }else{
                        findViewById<SuperRecyclerView>(R.id.user_view).adapter = null
                    }
                }
                return false
            }

        })
    }

    fun backtoFriendFragment(view: View) {
        this.finish()
    }

    private fun getAllUser(): List<FriendItem>? {
        val list = ArrayList<FriendItem>()
        var docId: String
        var friendlist: List<*>
        Log.wtf("current user", user.toString())
        db.collection("users").whereEqualTo("username", user.displayName)
            .addSnapshotListener { it, _ ->
                list.clear()
                if (!it?.isEmpty!!) {
                    friendlist = (it.documents.first().get("friends") as? List<*>) ?: emptyList<Any>()
                    db.collection("users").get().addOnSuccessListener {
                        var userid: String = ""
                        var username: String = ""
                        it.documents.forEach {
                            var photoUrl: String? = null
                            username = it.getString("username").toString()
                            if (it.getString("photoURL") != null) {
                                photoUrl = it.getString("photoURL")
                            }
                            userid = it.id

                            val isNotMyFriend = !friendlist.contains(userid)
                            val isNotMe = username != user.displayName

                            var res:Int = 0

                            if(it?.data?.get("plan") != null){
                                var plan = it?.data?.get("plan") as Map<*, *>
                                val date1 = Date()
                                val date2 = plan["startDate"] as Timestamp
                                var diff = date1.time - date2.toDate().time
                                res = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                                    .toInt()

                            }

                            if (isNotMyFriend && isNotMe) {
                                if (photoUrl == null) {
                                    var currentUser =
                                        FriendItem(
                                            username,
                                            false,
                                            null,
                                            res,
                                            userid,
                                            false
                                        )
                                    list.add(currentUser)
                                } else {
                                    var currentUser =
                                        FriendItem(
                                            username,
                                            true,
                                            photoUrl,
                                            res,
                                            userid,
                                            false
                                        )
                                    list.add(currentUser)
                                }
                                usernames.add(username)
                                findViewById<SuperRecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
                            }
                        }
                        val paginated = mutableListOf<FriendItem>()
                        paginated.addAll(list.take(8))

                        findViewById<SuperRecyclerView>(R.id.user_view).adapter =
                            FriendAdapter(list, this)
                        findViewById<SuperRecyclerView>(R.id.user_view)!!.setLayoutManager(LinearLayoutManager(this))
                        findViewById<SuperRecyclerView>(R.id.user_view)
                            ?.setupMoreListener({ overallItemsCount, itemsBeforeMore, maxLastVisiblePosition ->
                                if (maxLastVisiblePosition + 8 >= overallItemsCount - 1) {

                                }

                                val from = maxLastVisiblePosition + 1
                                paginated.clear()

                                var takenCount = 0
                                for (i in from until overallItemsCount) {
                                    if (takenCount >= 8) {
                                        break
                                    }

                                    paginated.add(list[i])
                                    takenCount++
                                }
                            }, 8)

                    }
                }
            }

        return list
    }

}