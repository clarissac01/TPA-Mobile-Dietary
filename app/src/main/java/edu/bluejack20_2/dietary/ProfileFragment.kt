package edu.bluejack20_2.dietary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import edu.bluejack20_2.dietary.services.login.LoginActivity

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        if (user != null) {
            updateProfilePic()

            requireActivity().findViewById<MaterialTextView>(R.id.username_text).text = user.displayName
            requireActivity().findViewById<MaterialTextView>(R.id.email_text).text = user.email
        }

        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            view.findViewById<MaterialTextView>(R.id.name_text).text = it.documents.first().getString("name")
            view.findViewById<MaterialTextView>(R.id.email_text).text = it.documents.first().getString("email")
            view.findViewById<MaterialTextView>(R.id.username_text).text = it.documents.first().getString("username")
        }

        requireActivity().findViewById<Button>(R.id.bmi_button).setOnClickListener {
            startActivity(Intent(requireContext(), DietPlanActivity::class.java))
        }

        requireActivity().findViewById<Button>(R.id.settings_button).setOnClickListener {
            startActivity(Intent(requireActivity(), SettingActivity::class.java))
        }

        requireActivity().findViewById<Button>(R.id.logout_button).setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

    }

    fun updateProfilePic() {
        db.collection("users").whereEqualTo("username", user.displayName).get()
            .addOnSuccessListener {
                if (it.documents.first().getString("photoURL") != null) {
                    Picasso.get().load(it.documents.first().getString("photoURL"))
                        .into(view?.findViewById<ImageView>(R.id.profile_pic))
                } else {
                    Picasso.get().load("@drawa ble/ic_photo")
                        .into(view?.findViewById<ImageView>(R.id.profile_pic))
                }
            }
    }

}