package edu.bluejack20_2.dietary

import android.graphics.drawable.Drawable
import android.icu.util.TimeUnit
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.ocpsoft.prettytime.PrettyTime
import org.w3c.dom.Text
import java.time.Duration
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()
    private lateinit var profilepic:CirleImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.wtf("duar", user?.toString())

        view.findViewById<TextView>(R.id.salutation).text = "Hello, " + user.displayName + "!"

        profilepic = view.findViewById<CirleImageView>(R.id.user_profilepic)

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(!it?.isEmpty!!){
                if(it.documents.first().get("plan") != null){
                    activatePlan(view)
                    val getMapping = it.documents.first().get("plan") as Map<*, *>
                    val date1 = Date()
                    val date2 = getMapping["startDate"] as Timestamp
                    var diff = date1.time - date2.toDate().time
                    var res = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                    view.findViewById<TextView>(R.id.plancurrday).text = res.toString()
                    view.findViewById<CircularProgressIndicator>(R.id.planprogresscircle).max =
                        30
                    view.findViewById<CircularProgressIndicator>(R.id.planprogresscircle).progress =
                        res.toLong().toInt()
                }else{
                    activateNoPlan(view)
                }
                if(it.documents.first().getString("photoURL") != null){
                    Picasso.get().load(it.documents.first().getString("photoURL")).into(profilepic)
                }
            }
        }

    }

    fun activatePlan(view: View){
        view.findViewById<TextView>(R.id.noplantext).visibility = View.INVISIBLE
        view.findViewById<ImageView>(R.id.noplanpic).visibility = View.INVISIBLE

        view.findViewById<TextView>(R.id.plan1).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.plan2).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.plan3).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.plan4).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.plancal).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.plancurrday).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.planday).visibility = View.VISIBLE
        view.findViewById<CircularProgressIndicator>(R.id.planprogresscircle).visibility = View.VISIBLE

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager2 = view.findViewById(R.id.viewPager)
        activity?.let {
            viewPager2.adapter = HomeMealAdapter(it)
        }

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

    fun activateNoPlan(view: View){
        view.findViewById<TextView>(R.id.noplantext).visibility = View.VISIBLE
        view.findViewById<ImageView>(R.id.noplanpic).visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.plan1).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.plan2).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.plan3).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.plan4).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.plancal).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.plancurrday).visibility = View.INVISIBLE
        view.findViewById<TextView>(R.id.planday).visibility = View.INVISIBLE
        view.findViewById<CircularProgressIndicator>(R.id.planprogresscircle).visibility = View.INVISIBLE

    }

}