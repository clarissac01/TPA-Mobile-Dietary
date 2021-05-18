package edu.bluejack20_2.dietary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class FriendAdapter(private val friendList: List<FriendItem>?, private val context: Context) :
    RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    private lateinit var friendUsername: String
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var friendDocId: String

    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val friendView =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return FriendHolder(friendView)
    }

    override fun getItemCount(): Int = friendList?.size!!

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        val friendItem = friendList?.get(position)
        friendDocId = friendItem?.docId.toString()

        holder.friendid.text = friendItem?.docId
        holder.delbtn.setOnClickListener {
            friendUsername = holder.friendname.text.toString()
            unfriend(it, friendItem?.docId.toString())
        }

        holder.addBtn.setOnClickListener {
            friendUsername = holder.friendname.text.toString()
            friend(it, holder, friendItem?.docId.toString())
        }

        if(friendItem?.username.equals(user.displayName)){
            holder.delbtn.visibility = View.INVISIBLE
            holder.addBtn.visibility = View.INVISIBLE
        }
        else if (friendItem?.isFriend == true) {
            holder.delbtn.visibility = View.VISIBLE
            holder.addBtn.visibility = View.INVISIBLE
        } else {
            holder.delbtn.visibility = View.INVISIBLE
            holder.addBtn.visibility = View.VISIBLE
        }

        val currentFriend = friendList?.get(position)

        if (currentFriend?.hasPhoto == true) {
            Picasso.get().load(currentFriend.photoUrl).into(holder.friendpic)
        } else {
            holder.friendpic.setImageResource(R.drawable.user)
        }
        if (currentFriend != null) {
            holder.friendname.text = currentFriend.username
        }
        if (currentFriend?.daycount!! > 0) {
            holder.daycount.text = "Day\n" + currentFriend.daycount
        }else{
            holder.daycount.visibility = View.INVISIBLE
        }

        holder.user_detail.setOnClickListener {
            var user: FriendItem
            if (currentFriend?.hasPhoto!!) {
                user = FriendItem(
                    currentFriend?.username!!,
                    currentFriend.hasPhoto,
                    currentFriend.photoUrl,
                    currentFriend.daycount,
                    currentFriend.docId,
                    currentFriend.isFriend
                )
            } else {
                user = FriendItem(
                    currentFriend?.username!!,
                    currentFriend.hasPhoto,
                    null,
                    currentFriend.daycount,
                    currentFriend.docId,
                    currentFriend.isFriend
                )
            }
            gotoUserDetail(context, user)
        }

    }

    class FriendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendpic: ImageView = itemView.findViewById(R.id.friendpic)
        val friendname: TextView = itemView.findViewById(R.id.friendname)
        val daycount: MaterialButton = itemView.findViewById(R.id.daycountbtn)
        val delbtn = itemView.findViewById<MaterialButton>(R.id.deletefriend)
        val addBtn = itemView.findViewById<MaterialButton>(R.id.addfriend)
        val user_detail = itemView.findViewById<CardView>(R.id.user_detail)
        val friendid = itemView.findViewById<TextView>(R.id.thisfriendid)
    }

    fun unfriend(view: View, friendID: String) {
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
                            .update("friends", FieldValue.arrayRemove(friendID))
                            .addOnSuccessListener {

                            }
                    }
            }
            .show()
    }

    fun friend(view: View, holder: FriendHolder, friendID: String) {
        db.collection("users").whereEqualTo("username", user.displayName).get()
            .addOnSuccessListener {
                var userid: String = ""
                it.documents.forEach {
                    userid = it.id
                }
                db.collection("users").document(userid)
                    .update("friends", FieldValue.arrayUnion(friendID)).addOnSuccessListener {
                        holder.delbtn.visibility = View.VISIBLE
                        holder.addBtn.visibility = View.INVISIBLE
                    }
            }
    }

    fun gotoUserDetail(context: Context, user: FriendItem) {
        Log.wtf("gotouser detail", user.toString())
        val intent = Intent(
            context,
            UserDetail::class.java
        )
        intent.putExtra("user", user)
        context.startActivity(
            intent
        )
    }
}