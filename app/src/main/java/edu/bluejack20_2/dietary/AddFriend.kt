package edu.bluejack20_2.dietary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddFriend : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    lateinit var search:SearchView
    var usernames: MutableList<String> = mutableListOf()
    lateinit var friendlist: List<FriendItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        friendlist = getAllUser()!!
        findViewById<RecyclerView>(R.id.user_view).adapter = FriendAdapter(friendlist, this)
        findViewById<RecyclerView>(R.id.user_view).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<RecyclerView>(R.id.user_view).setHasFixedSize(true)

        search = findViewById<SearchView>(R.id.search_friend)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                    var filter = friendlist?.filter {
                        it.username.contains(query.toString())
                    }
                    Log.wtf("filter", filter.toString())
                    if(filter.isEmpty()){
                        Toast.makeText(applicationContext, "User not found!", Toast.LENGTH_LONG).show()
                    }else{
                        var alist = friendlist?.toMutableList()
                        alist.clear()
                        alist.addAll(filter)
                        findViewById<RecyclerView>(R.id.user_view).adapter = FriendAdapter(alist, this@AddFriend)
                        findViewById<RecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()

                    }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search.clearFocus()
                if(newText.equals("")){
                    findViewById<RecyclerView>(R.id.user_view).adapter = FriendAdapter(friendlist, this@AddFriend)
                    findViewById<RecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
                }else{
                    var filter = friendlist?.filter {
                        it.username.contains(newText.toString())
                    }
                    Log.wtf("filter", filter.toString())
                    if(!filter.isEmpty()){
                        var alist = friendlist?.toMutableList()
                        alist.clear()
                        alist.addAll(filter)
                        findViewById<RecyclerView>(R.id.user_view).adapter = FriendAdapter(alist, this@AddFriend)
                        findViewById<RecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
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
                    friendlist = it.documents.first().get("friends") as List<*>
                    db.collection("users").get().addOnSuccessListener {
                        var userid: String = ""
                        var username: String = ""
                        it.documents.forEach {
                            var photoUrl: String? = null
                            username = it.getString("username").toString()
                            if (it.getString("photoURL") != null) {
                                photoUrl = it.getString("photoURL")
                            }
                            Log.wtf("ini fotonya", photoUrl)
                            userid = it.id

                            val isNotMyFriend = !friendlist.contains(userid)
                            val isNotMe = username != user.displayName

                            Log.wtf("hehe", isNotMyFriend.toString())
                            Log.wtf("hehe", isNotMe.toString())

                            if (isNotMyFriend && isNotMe) {
                                if (photoUrl == null) {
                                    var currentUser = FriendItem(username, false, null, 1, userid, false)
                                    list.add(currentUser)
                                } else {
                                    var currentUser = FriendItem(username, true, photoUrl, 1, userid, false)
                                    list.add(currentUser)
                                }
                                usernames.add(username)
                                findViewById<RecyclerView>(R.id.user_view).adapter?.notifyDataSetChanged()
                            }
                        }
                        Log.wtf("username2", usernames.toString())
                    }
                }
            }

        return list
    }

}