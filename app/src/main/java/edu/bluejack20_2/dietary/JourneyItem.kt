package edu.bluejack20_2.dietary

import android.net.Uri
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JourneyItem(
    val journeyId: String,
    val date: Timestamp,
    val friendId: String,
    val calories: Int,
    var isBreakfastExpand: Boolean = false,
    var isLunchExpand: Boolean = false,
    var isDinnerExpand: Boolean = false,
    var isSnackExpand: Boolean = false
) : Parcelable
