package edu.bluejack20_2.dietary

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseUserDetailPageNavigator(fragmentActivity: FragmentActivity, var friendId: String) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        when(position){
            0 -> {
                return FriendCustomMeal(friendId)
            }
            else -> {
                return FriendJourney(friendId)
            }
        }
    }

}