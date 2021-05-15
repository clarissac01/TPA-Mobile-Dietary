package edu.bluejack20_2.dietary

import android.net.Uri
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FriendItem(
    val username: String,
    val hasPhoto: Boolean,
    val photoUrl: String?,
    val daycount: Int,
    val docId: String,
    val isFriend: Boolean = false
) : Parcelable
