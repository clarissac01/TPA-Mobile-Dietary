package edu.bluejack20_2.dietary

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso

class UserDetail() : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var username:TextView
    private lateinit var userDocId:String
    private var friends: List<Any>? = null
    private var isFriend:Boolean = false
    private lateinit var addfriendbtn:MaterialButton
    private lateinit var unfriendbtn:MaterialButton
    private lateinit var profilepic:CirleImageView
    private lateinit var userFriend: FriendItem

    override fun onCreate(savedInstanceState: Bundle?) {
        var user_detail:FriendItem? = getIntent().getExtras()?.get("user") as FriendItem?

        userFriend = user_detail!!
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        username = findViewById(R.id.user_id)
        username.text = user_detail?.username!!
        userDocId = user_detail.docId
        isFriend = user_detail.isFriend
        addfriendbtn = findViewById(R.id.addfriendbtn)
        unfriendbtn = findViewById(R.id.unfriend_btn)
        profilepic = findViewById(R.id.user_profilepic)

        addfriendbtn.setOnClickListener {
            friend(it)
        }
        unfriendbtn.setOnClickListener {
            unfriend(it)
        }

        db.collection("users").document(userDocId).addSnapshotListener { it, _ ->
            friends = it?.get("friends") as List<Any>?
            var friendcount = findViewById<Button>(R.id.friend_count)
            var countfriend = "0\nFRIENDS"
            if(friends?.size != null){
                Log.wtf("count friend", friends.toString() + " " + it?.getString("username"))
                countfriend = friends?.size.toString()+"\nFRIENDS"
            }
            friendcount.text = countfriend
            Log.wtf("count friend", friends.toString() + " " + it?.getString("username"))
        }

        findViewById<TextView>(R.id.friend_count).setOnClickListener{
            seeUserFriend(it)
        }

        if(isFriend){
            unfriendbtn.visibility = View.VISIBLE
            addfriendbtn.visibility = View.INVISIBLE
        }else{
            unfriendbtn.visibility = View.INVISIBLE
            addfriendbtn.visibility = View.VISIBLE
        }

        if(user_detail.hasPhoto){
            Picasso.get().load(user_detail.photoUrl).into(profilepic)
        }

    }

    fun back(view: View) {
        this.finish()
    }

    fun unfriend(view: View) {
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Are you sure?")
            .setPositiveButton("NO") { dialog, which ->
                // Respond to negative button press
            }
            .setNegativeButton("YES") { dialog, which ->
                // Respond to positive button press
                db.collection("users").whereEqualTo("username", user.displayName).get()
                    .addOnSuccessListener {
                        var userid: String = ""
                        it.documents.forEach {
                            userid = it.id
                        }
                        db.collection("users").document(userid)
                            .update("friends", FieldValue.arrayRemove(userDocId))
                            .addOnSuccessListener {
                                unfriendbtn.visibility = View.INVISIBLE
                                addfriendbtn.visibility = View.VISIBLE
                            }
                    }
            }
            .show()
    }

    fun friend(view: View) {
        db.collection("users").whereEqualTo("username", user.displayName).get()
            .addOnSuccessListener {
                var userid: String = ""
                it.documents.forEach {
                    userid = it.id
                }
                db.collection("users").document(userid)
                    .update("friends", FieldValue.arrayUnion(userDocId)).addOnSuccessListener {
                        unfriendbtn.visibility = View.VISIBLE
                        addfriendbtn.visibility = View.INVISIBLE
                    }
            }
    }

    fun seeUserFriend(view: View){
        val intent = Intent(
            this,
            UserFriend::class.java
        )
        Log.wtf("see this user friend detail", userFriend.username)
        intent.putExtra("user", userFriend)
        this.startActivity(
            intent
        )
    }

}