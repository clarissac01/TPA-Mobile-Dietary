package edu.bluejack20_2.dietary

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class FriendAdapter(private val friendList: List<FriendItem>): RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val friendView = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)

        return FriendHolder(friendView)
    }

    override fun getItemCount() = friendList.size

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        val currentFriend = friendList[position]

        if(currentFriend.hasPhoto==true){
            holder.friendpic.setImageURI(currentFriend.photoUrl)
        }else{
            holder.friendpic.setImageResource(R.drawable.user)
        }
        holder.friendname.text = currentFriend.username
        holder.daycount.text = "Day\n"+currentFriend.daycount

    }

    class FriendHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val friendpic: ImageView = itemView.findViewById(R.id.friendpic)
        val friendname: TextView = itemView.findViewById(R.id.name_text)
        val daycount: MaterialButton = itemView.findViewById(R.id.daycountbtn)
    }
}