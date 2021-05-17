package edu.bluejack20_2.dietary

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.concurrent.Executor


class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var db = FirebaseFirestore.getInstance()

    private lateinit var userPic: CirleImageView
    private lateinit var userPicURI: Uri
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userUsername: String
    private lateinit var userConfirmPassword: String
    private var bitmap: Bitmap? = null
    private var currentEmail: String = ""
    private var currentUsername: String = ""
    private var currentPassword: String = ""

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        val userFire = FirebaseFirestore.getInstance().collection("users").whereEqualTo("username", user.displayName).get().addOnSuccessListener {
            if(it.documents.first().getString("password") == null) {
//                Log.wtf("uid", user.uid)
                Log.wtf("heh", it.documents.first().getString("password"))
                findViewById<TextInputLayout>(R.id.user_email_field).isEnabled = false
                findViewById<TextInputLayout>(R.id.user_password_field).isEnabled = false
                findViewById<TextInputLayout>(R.id.user_confirm_password_field).isEnabled = false
            }
        }


        if (user != null) {
            findViewById<TextInputLayout>(R.id.user_username_field).hint = user.displayName
            findViewById<TextInputLayout>(R.id.user_email_field).hint = user.email

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(findViewById<ImageView>(R.id.profile_pic))
            } else {
                Picasso.get().load("@drawable/ic_photo")
                    .into(findViewById<ImageView>(R.id.profile_pic))
            }

            findViewById<TextInputEditText>(R.id.user_username_text).setOnFocusChangeListener { v, hasFocus ->
                findViewById<TextInputLayout>(R.id.user_username_field).hint =
                    if (hasFocus) "Username" else user.displayName
            }

            findViewById<TextInputEditText>(R.id.user_email_text).setOnFocusChangeListener { v, hasFocus ->
                findViewById<TextInputLayout>(R.id.user_email_field).hint =
                    if (hasFocus) "Email" else user.email
            }

            currentEmail = user.email
            currentUsername = user.displayName

            db.collection("users").whereEqualTo("username", currentUsername).get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        currentPassword = it.getString("password").toString()
                    }
                }

        }

        findViewById<Button>(R.id.update_button).setOnClickListener {
            updateValidation(it)
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun changeImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            userPicURI = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, userPicURI)
            userPic.setImageBitmap(bitmap!!)
        }
    }

    fun updateValidation(view: View) {
        val email: TextInputEditText = findViewById(R.id.user_email_text)
        val password: TextInputEditText = findViewById(R.id.user_password_text)
        val confirmPassword: TextInputEditText = findViewById(R.id.user_confirm_password_text)
        val username: TextInputEditText = findViewById(R.id.user_username_text)
        userEmail = email.text.toString().trim()
        userPassword = password.text.toString()
        userConfirmPassword = confirmPassword.text.toString()
        userUsername = username.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$"
        if (userEmail == "") {
            userEmail = currentEmail
        }
        if (userUsername == "") {
            userUsername = currentUsername
        }
        if (userPassword == "") {
            userPassword = currentPassword
        }
        if (userConfirmPassword == "") {
            userConfirmPassword = currentPassword
        }
//        updateUser(userUsername, userEmail, userPassword, bitmap.toString())
        if (userPassword != "" && userConfirmPassword != "") {
            if (!userConfirmPassword.matches(passPattern.toRegex())) {
                return Toast.makeText(this, "Password must be alphanumeric!", Toast.LENGTH_SHORT)
                    .show()
            }
            if (isLettersOrDigits(userConfirmPassword) && userConfirmPassword.length >= 8) {
                updateUser(userUsername, userEmail, userPassword, bitmap.toString())
            } else {
                return Toast.makeText(
                    this,
                    "Password must be more than 7 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (userEmail != "") {
            if (!userEmail.matches(emailPattern.toRegex())) {
                return Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isLettersOrDigits(chars: String): Boolean {
        return chars.filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
            .length == chars.length
    }

    fun updateUser(name: String, email: String, password: String, image: String? = null) {
        SafetyNet.getClient(this).verifyWithRecaptcha("6LfJCNYaAAAAAF7Dte27iGN9jt0iOeTUo3BJrh8x")
            .addOnSuccessListener { response ->
                val userResponseToken = response.tokenResult
                val oldUname = user.displayName
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("username", oldUname)
                    .get()
                    .addOnFailureListener {
                        Log.wtf("hehe", it.toString())
                    }
                    .addOnSuccessListener { q ->
//                Log.wtf("hehehehe", q.documents.toString())
                        val doc = q.documents.first()

                        val oldPassword = doc.getString("password")
                        Log.wtf("id", doc.id)
                        Log.wtf("hhhh", (oldPassword == null).toString())
                        Log.wtf("hadoh", doc.getString("username"))
                        Log.wtf("uname", oldUname)
                        Log.wtf("old", oldPassword)
                        Log.wtf("pass", password)

                        if (oldPassword != null && oldPassword != password) {
                            Toast.makeText(this, "Old Password doesn't match!", Toast.LENGTH_SHORT)
                                .show()
                            return@addOnSuccessListener
                        }

                        Log.wtf("hehe", listOf(userConfirmPassword, email, name).toString())
//                val credential = GoogleAuthProvider
//                    .getCredential("user@example.com", "password1234")
                        val data = mutableListOf(
                            user.updateEmail(email).addOnFailureListener {
                            Log.wtf("hehe", it.toString())
                        },
                            user.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                            ).addOnFailureListener {
                                Log.wtf("hehe", it.toString())
                            },
                            doc.reference.update(
                                mapOf(
                                    "email" to email,
                                    "password" to userConfirmPassword,
                                    "username" to name
                                )
                            ).addOnFailureListener {
                                Log.wtf("hehe", it.toString())
                            })
                        if(doc.getString("password") != null) {
                            data.add(FirebaseAuth.getInstance().currentUser.updatePassword(userConfirmPassword).addOnFailureListener {
                                Log.wtf("hehe", it.toString())
                            })
                        }
                        Tasks.whenAll(data)

                            .addOnFailureListener {
                                Log.wtf("hehe", it.toString())
                            }
                            .addOnSuccessListener {
                                Toast.makeText(
                                    EditProfile@ this,
                                    "Update Success",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
            }
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    Log.d("TAG", "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    Log.d("TAG", "Error: ${e.message}")
                }
            }
    }

    fun onClick(view: View) {

    }


    fun gotoEditProfile(view: View) {
        startActivity(Intent(this, EditProfile::class.java))
    }
}