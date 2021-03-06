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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.malinskiy.superrecyclerview.SuperRecyclerView
import java.lang.reflect.Field
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    var db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserFriends()
    }

    override fun onResume() {
        super.onResume()
        getUserFriends()
    }

    private fun getUserFriends() {
        val list = ArrayList<FriendItem>()
        var docId: String
        db.collection("users").whereEqualTo("username", user.displayName)
            .addSnapshotListener { it, _ ->
                list.clear()
                if (!it?.isEmpty!!) {
                    var friendlist = it.documents.first().get("friends") as List<*>?
                    if (friendlist?.size == 0) {
                        requireActivity().findViewById<SuperRecyclerView>(R.id.friend_view).adapter =
                            null
                    } else {
                    friendlist?.forEach {
                        var docId = it.toString()
                        db.collection("users").document(docId)
                            .addSnapshotListener { it, _ ->
                                var username = it?.data?.getValue("username")
                                var photo = it?.data?.get("photoURL")

                                var res: Int = 0

                                if (it?.data?.get("plan") != null) {
                                    var plan = it?.data?.get("plan") as Map<*, *>
                                    val date1 = Date()
                                    val date2 = plan["startDate"] as Timestamp
                                    var diff = date1.time - date2.toDate().time
                                    res = java.util.concurrent.TimeUnit.DAYS.convert(
                                        diff,
                                        java.util.concurrent.TimeUnit.MILLISECONDS
                                    )
                                        .toInt()

                                }

                                if (photo == null) {
                                    var friend =
                                        FriendItem(
                                            username.toString(),
                                            false,
                                            null,
                                            res,
                                            docId,
                                            true
                                        )
                                    list += friend
                                } else {
                                    var friend =
                                        FriendItem(
                                            username.toString(),
                                            true,
                                            photo.toString(),
                                            res,
                                            docId,
                                            true
                                        )
                                    list += friend
                                }
                                view?.findViewById<SuperRecyclerView>(R.id.friend_view)?.adapter?.notifyDataSetChanged()
                            }
                    }


                        val paginated = mutableListOf<FriendItem>()
                        paginated.addAll(list.take(7))

                        view?.findViewById<SuperRecyclerView>(R.id.friend_view)!!.adapter = FriendAdapter(list, requireContext())
                        view?.findViewById<SuperRecyclerView>(R.id.friend_view)!!.setLayoutManager(LinearLayoutManager(context))
                        view?.findViewById<SuperRecyclerView>(R.id.friend_view)
                            ?.setupMoreListener({ overallItemsCount, itemsBeforeMore, maxLastVisiblePosition ->
                                if (maxLastVisiblePosition + 7 >= overallItemsCount - 1) {

                                }

                                val from = maxLastVisiblePosition + 1
                                paginated.clear()

                                var takenCount = 0
                                for (i in from until overallItemsCount) {
                                    if (takenCount >= 7) {
                                        break
                                    }

                                    paginated.add(list[i])
                                    takenCount++
                                }
                            }, 7)



                }
                }
            }

    }


}