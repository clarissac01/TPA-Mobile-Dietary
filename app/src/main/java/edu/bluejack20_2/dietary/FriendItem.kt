package edu.bluejack20_2.dietary

import android.net.Uri

data class FriendItem(
    val username:String,
    val hasPhoto: Boolean,
    val photoUrl: Uri?,
    val daycount: Int
)
