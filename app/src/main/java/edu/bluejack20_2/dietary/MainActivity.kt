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
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onStart() {
//        FirebaseAuth.getInstance().signOut()


        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        updateUI(account)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }else{
            val account = GoogleSignIn.getLastSignedInAccount(this)
        }


        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
//        val customMealFragment = CustomMealFragment()
        val friendsFragment = FriendsFragment()
        val journeyFragment = JourneyFragment()
        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).bringToFront()

        setCurrentFragment(homeFragment)

        findViewById<BubbleNavigationConstraintView>(R.id.bottom_navigation_view_linear).setNavigationChangeListener {view, position ->
            when(view.id) {
                R.id.l_item_home -> setCurrentFragment(homeFragment)
//                R.id.l_item_custom_meal -> setCurrentFragment(customMealFragment)
                R.id.l_item_profile -> setCurrentFragment(profileFragment)
                R.id.l_item_friend -> setCurrentFragment(friendsFragment)
                R.id.l_item_journey -> setCurrentFragment(journeyFragment)
            }
        }

//        findViewById<Button>(R.id.main_ingredients_button).setOnClickListener {
//            gotoMainIngredients(it)
//        }
    }


    fun setCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }


    fun gotoMainIngredients(view: View) {
        startActivity(Intent(this, MainIngredients::class.java) )
    }

    fun gotoEditProfile(view: View) {
        startActivity(Intent(this, EditProfile::class.java))
    }

    fun addFriend(view: View) {
        startActivity(Intent(this, AddFriend::class.java))
    }
}