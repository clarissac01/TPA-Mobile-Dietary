package edu.bluejack20_2.dietary

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.reflect.Field
import java.net.URI

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendlist = getUserFriends()
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).adapter = FriendAdapter(friendlist)
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).layoutManager = LinearLayoutManager(requireContext())
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).setHasFixedSize(true)
    }

    private fun getUserFriends():List<FriendItem>{
        val list = ArrayList<FriendItem>()
        var docId: String
        db.collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            var friendlist = it.documents.first().get("friends") as List<*>
            friendlist.forEach{
                db.collection("users").document(it.toString()).get().addOnSuccessListener {
                    if(it.get("photoURL")!=null){
                        var friend = it.getString("username")?.let { it1 -> FriendItem(it1, true, it.getString("photoURL") as Uri, 1) }
                        if (friend != null) {
                            list.add(friend)
                        }
                    }else{
                        var friend =
                            it.getString("username")?.let { it1 -> FriendItem(it1, false, null, 1) }
                        if (friend != null) {
                            list.add(friend)
                        }
                    }
                }
            }
        }

        return list
    }


}