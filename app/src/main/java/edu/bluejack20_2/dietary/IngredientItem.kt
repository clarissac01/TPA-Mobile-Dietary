package edu.bluejack20_2.dietary

import android.net.Uri
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IngredientItem(
    val ingredientId: String,
    var ingredientName: String,
    var calory: Float,
    var weight: Float
) : Parcelable
