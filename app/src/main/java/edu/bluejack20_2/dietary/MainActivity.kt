package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
//        updateUI(account)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val customMealFragment = CustomMealFragment()
        val friendsFragment = FriendsFragment()
        val journeyFragment = JourneyFragment()

        setCurrentFragment(homeFragment)

        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).setNavigationChangeListener {view, position ->
            when(view.id) {
                R.id.l_item_home -> setCurrentFragment(homeFragment)
                R.id.l_item_custom_meal -> setCurrentFragment(customMealFragment)
                R.id.l_item_profile -> setCurrentFragment(profileFragment)
                R.id.l_item_friend -> setCurrentFragment(friendsFragment)
                R.id.l_item_journey -> setCurrentFragment(journeyFragment)
            }
            true
        }
    }


    private fun setCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }


    fun gotoRegister(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun gotoEditProfile(view: View) {
        startActivity(Intent(this, EditProfile::class.java))
    }
}