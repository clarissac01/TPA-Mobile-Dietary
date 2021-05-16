package edu.bluejack20_2.dietary

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeMealAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var breakfastMenu:String
    private lateinit var lunchMenu:String
    private lateinit var dinnerMenu:String
    private lateinit var snackMenu:String

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {

        when(position){
            0 -> {
                return BreakfastFragment()
            }
            1 -> {
                return LunchFragment()
            }
            2 -> {
                return DinnerFragment()
            }
            else -> {
                return SnackFragment()
            }
        }
    }

}