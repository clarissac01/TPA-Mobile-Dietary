package edu.bluejack20_2.dietary

import android.os.Parcelable
import android.text.BoringLayout
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MealItem(
    val mealId: String,
    var mealName: String,
    var mealCalories: Float,
    var isExpand: Boolean,
    var hasMeal: Boolean = false
) : Parcelable
