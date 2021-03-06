package edu.bluejack20_2.dietary.services.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack20_2.dietary.MainActivity
import edu.bluejack20_2.dietary.R
import edu.bluejack20_2.dietary.services.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    private lateinit var userUsername: String
    private lateinit var userPassword: String

    private lateinit var googleSingInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        googleSingInClient = GoogleSignIn.getClient(this, gso)
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            logIn(it)
        }
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            signIn()
        };
    }

    fun gotoRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun logIn(view: View) {
        userUsername = findViewById<TextInputEditText>(R.id.usernameText).text.toString().trim()
        userPassword = findViewById<TextInputEditText>(R.id.passwordText).text.toString()
        if (userUsername == "") {
            findViewById<TextView>(R.id.nametextnull).visibility = View.VISIBLE
        }else{
            findViewById<TextView>(R.id.nametextnull).visibility = View.INVISIBLE
        }
        if (userPassword == "") {
            findViewById<TextView>(R.id.passwordTextnull).visibility = View.VISIBLE
            return
        }else{
            findViewById<TextView>(R.id.passwordTextnull).visibility = View.INVISIBLE
        }
        userUsername = userUsername.toLowerCase()
        db.collection("users")
                .whereEqualTo("username", userUsername).whereEqualTo("password", userPassword).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Toast.makeText(this, getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show()

                        var email:String? = ""
                        var photourl:Uri? = null
                        var password:String? = ""
                        for (i in it.documents) {
                            if (i.getString("email") != null) {
                                email = i.getString("email")!!
                            }
                            if (i.get("photoURL") != null) {
                                photourl = Uri.parse(i.get("photoURL") as String?)

                            }
                            if(i.getString("password")!=null){
                                password = i.getString(i.getString("password")!!)
                            }
                        }

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, userPassword).addOnSuccessListener {
                            if(it != null){
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(userUsername).setPhotoUri(photourl).build()).addOnSuccessListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                }.addOnFailureListener {
                                    Log.wtf("error this", it.toString())
                                }
                            }else{
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, userPassword).addOnSuccessListener {
                                    Log.wtf("errorrree2", it.toString())
                                    FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(userUsername).setPhotoUri(photourl).build()).addOnSuccessListener {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }.addOnFailureListener {
                                        Log.wtf("error", it.toString())
                                    }
                                }
                            }
                        }.addOnFailureListener{
                            Log.wtf("13 errornya", it.toString())
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, userPassword).addOnSuccessListener {
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(userUsername).setPhotoUri(photourl).build()).addOnSuccessListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                }.addOnFailureListener {
                                    Log.wtf("12", it.toString())
                                }
                            }.addOnFailureListener {
                            }
                        }
                    }
                }

    }

    private fun signIn() {
        val signInIntent: Intent = googleSingInClient.getSignInIntent()
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show()

            completedTask.addOnSuccessListener {
                val name = it.displayName?.replace(" ", "-")
                val email = it.email
                val photo = it.photoUrl

                db.collection("users")
                        .whereEqualTo("email", it.email).get().addOnSuccessListener {
                            if (it.isEmpty) {
                                val user: MutableMap<String, Any?> = HashMap()
                                user["username"] = name
                                user["email"] = email
                                if (photo != null) {
                                    user["photoURL"] = photo
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener { documentReference -> Log.d("ok", "DocumentSnapshot added with ID: " + documentReference.id) }
                                            .addOnFailureListener { e -> Log.w("ok", "Error adding document", e) }
                                } else {
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener { documentReference -> Log.d("ok", "DocumentSnapshot added with ID: " + documentReference.id) }
                                            .addOnFailureListener { e -> Log.w("ok", "Error adding document", e) }
                                }
                            }

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, email).addOnSuccessListener {
                            if(it != null){
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build()).addOnSuccessListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Log.wtf("wtf", "Masuk")
                                }.addOnFailureListener {
                                    Log.wtf("error", it.toString())
                                }
                            }else{
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, email).addOnSuccessListener {
                                    FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build()).addOnSuccessListener {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        Log.wtf("wtf", "Masuk")
                                    }.addOnFailureListener {
                                        Log.wtf("error", it.toString())
                                    }
                                }
                            }
                        }.addOnFailureListener{
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, email).addOnSuccessListener {
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build()).addOnSuccessListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Log.wtf("wtf", "Masuk")
                                }.addOnFailureListener {
                                    Log.wtf("error", it.toString())
                                }
                            }
                        }
                    }
            }
        } catch (e: ApiException) {
            Log.wtf("error", e.toString())
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(FragmentActivity.TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
            Toast.makeText(this, getString(R.string.failed_to_login), Toast.LENGTH_SHORT).show()
        }
    }


}