package edu.bluejack20_2.dietary

import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()
    private lateinit var profilepic:CirleImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<TextView>(R.id.salutation).text = "Hello, " + user.displayName + "!"

        profilepic = requireActivity().findViewById<CirleImageView>(R.id.user_profilepic)

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(!it?.isEmpty!!){
                if(it.documents.first().get("plan") != null){
                    activatePlan()
                    val getMapping = it.documents.first().get("plan") as Map<*, *>
                    requireActivity().findViewById<TextView>(R.id.planday).text = getMapping["duration"].toString()
                    requireActivity().findViewById<TextView>(R.id.plancurrday).text = getMapping["currentDay"].toString()
                    requireActivity().findViewById<CircularProgressIndicator>(R.id.planprogresscircle).max =
                        getMapping["duration"].toString().toInt()
                    requireActivity().findViewById<CircularProgressIndicator>(R.id.planprogresscircle).progress = getMapping["currentDay"].toString().toInt()
                }else{
                    activateNoPlan()
                }
                if(it.documents.first().getString("photoURL") != null){
                    Picasso.get().load(it.documents.first().getString("photoURL")).into(profilepic)
                }
            }
        }

    }

    fun activatePlan(){
        requireActivity().findViewById<TextView>(R.id.noplantext).visibility = View.INVISIBLE
        requireActivity().findViewById<ImageView>(R.id.noplanpic).visibility = View.INVISIBLE

        requireActivity().findViewById<TextView>(R.id.plan1).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.plan2).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.plan3).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.plan4).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.plancal).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.plancurrday).visibility = View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.planday).visibility = View.VISIBLE
        requireActivity().findViewById<CircularProgressIndicator>(R.id.planprogresscircle).visibility = View.VISIBLE

        tabLayout = requireActivity().findViewById(R.id.tabLayout)
        viewPager2 = requireActivity().findViewById(R.id.viewPager)

        viewPager2.adapter = HomeMealAdapter(requireActivity())

        tabLayout.addTab(tabLayout.newTab().setText("Breakfast"))
        tabLayout.addTab(tabLayout.newTab().setText("Lunch"))
        tabLayout.addTab(tabLayout.newTab().setText("Dinner"))
        tabLayout.addTab(tabLayout.newTab().setText("Snack"))

        TabLayoutMediator(tabLayout, viewPager2){tab, position->
            when(position){
                0 -> tab.text = "Breakfast"
                1 -> tab.text = "Lunch"
                2 -> tab.text = "Dinner"
                3 -> tab.text = "Snack"
            }
        }.attach()
    }

    fun activateNoPlan(){
        requireActivity().findViewById<TextView>(R.id.noplantext).visibility = View.VISIBLE
        requireActivity().findViewById<ImageView>(R.id.noplanpic).visibility = View.VISIBLE

        requireActivity().findViewById<TextView>(R.id.plan1).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.plan2).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.plan3).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.plan4).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.plancal).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.plancurrday).visibility = View.INVISIBLE
        requireActivity().findViewById<TextView>(R.id.planday).visibility = View.INVISIBLE
        requireActivity().findViewById<CircularProgressIndicator>(R.id.planprogresscircle).visibility = View.INVISIBLE

    }

}