package edu.bluejack20_2.dietary

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).adapter =
            FriendAdapter(friendlist, requireContext())
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        requireActivity().findViewById<RecyclerView>(R.id.friend_view).setHasFixedSize(true)

    }

    private fun getUserFriends(): List<FriendItem>? {
        val list = ArrayList<FriendItem>()
        var docId: String
        Log.wtf("current user name", user.displayName)
        db.collection("users").whereEqualTo("username", user.displayName)
            .addSnapshotListener { it, _ ->
                list.clear()
                if (!it?.isEmpty!!) {
                    var friendlist = it.documents.first().get("friends") as List<*>
                    friendlist.forEach {
                        var docId = it.toString()
                        db.collection("users").document(docId)
                            .addSnapshotListener { it, _ ->
                                var username = it?.data?.getValue("username")
                                var photo = it?.data?.get("photoURL")
                                Log.wtf("fotonya", photo.toString())

                                if (photo == null) {
                                    var friend =
                                        FriendItem(username.toString(), false, null, 1, docId, true)
                                    list += friend
                                } else {
                                    var friend =
                                        FriendItem(
                                            username.toString(),
                                            true,
                                            photo.toString(),
                                            1,
                                            docId,
                                            true
                                        )
                                    list += friend
                                }
                                requireActivity().findViewById<RecyclerView>(R.id.friend_view).adapter?.notifyDataSetChanged()
                            }
                    }
                }
            }

        return list
    }


}