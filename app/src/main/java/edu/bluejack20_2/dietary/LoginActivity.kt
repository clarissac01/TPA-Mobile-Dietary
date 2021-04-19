package edu.bluejack20_2.dietary

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
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
import java.io.ByteArrayOutputStream


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
            return Toast.makeText(this, "You must input Username!", Toast.LENGTH_SHORT).show()
        }
        if (userPassword == "") {
            return Toast.makeText(this, "You must input Password!", Toast.LENGTH_SHORT).show()
        }
        db.collection("users")
                .whereEqualTo("username", userUsername).whereEqualTo("password", userPassword).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show()
                    } else {
                        for (i in it.documents) {
                            if (i.getString("email") != null) {
                                FirebaseAuth.getInstance().currentUser.updateEmail(i.getString("email"))
                            }
                            if (i.get("photoURL") != null) {
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(userUsername).setPhotoUri(i.get("photoURL") as Uri?).build())
                            }
                        }
                        Toast.makeText(this, "Success Login!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }

    }

    private fun signIn() {
        val signInIntent: Intent = googleSingInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
            Toast.makeText(this, "Success Login!", Toast.LENGTH_SHORT).show()

            completedTask.addOnSuccessListener {
                val name = it.displayName?.replace(" ", "-")
                val email = it.email
                val photo = it.photoUrl

                db.collection("users")
                        .whereEqualTo("email", it.displayName).get().addOnSuccessListener {
                            if (!it.isEmpty) {
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
                                FirebaseAuth.getInstance().currentUser.updateEmail(email)
                                FirebaseAuth.getInstance().currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build())
                                startActivity(Intent(this, MainActivity::class.java))
                            }


                        }
            }
        } catch (e: ApiException) {
            Log.wtf("error", e.toString())
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(FragmentActivity.TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
            Toast.makeText(this, "Failed to Login!", Toast.LENGTH_SHORT).show()
        }
    }


}