package edu.bluejack20_2.dietary

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var db = FirebaseFirestore.getInstance()
    var storageRef: StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var userPic: CirleImageView
    private lateinit var userPicURI: Uri
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userUsername: String
    private var bitmap: Bitmap? = null
    private lateinit var currentEmail: String
    private lateinit var currentUsername: String
    private lateinit var updatedEmail: String
    private lateinit var updatedUsername: String

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        user = auth.currentUser
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(findViewById<ImageView>(R.id.profile_pic))
            } else {
                Picasso.get().load("@drawable/ic_photo")
                    .into(findViewById<ImageView>(R.id.profile_pic))
            }

            findViewById<TextView>(R.id.user_email_field).setHint(user.email)
            findViewById<TextView>(R.id.user_userame_field).setHint(user.displayName)

            currentEmail = user.email
            currentUsername = user.displayName
        }

        findViewById<Button>(R.id.update_button).setOnClickListener {

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
        Log.wtf("wtf", "wtf")
        val email: EditText = findViewById(R.id.user_email_field)
        val password: EditText = findViewById(R.id.user_password_field)
        val username: EditText = findViewById(R.id.user_userame_field)
        userEmail = email.text.toString().trim()
        userPassword = password.text.toString()
        userUsername = username.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passPattern = "[a-zA-Z0-9]"
        if (userEmail == currentEmail) {
            updatedEmail = currentEmail
        }
        if (userUsername == currentUsername) {
            updatedUsername = currentUsername
        }
        if (userEmail.matches(emailPattern.toRegex())) {
            if (isLettersOrDigits(userPassword) && userPassword.length >= 8) {
                updateUser(userUsername, userEmail, userPassword, bitmap.toString())
            } else if (!userPassword.matches(passPattern.toRegex())) {
                return Toast.makeText(this, "Password must be alphanumeric!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                return Toast.makeText(
                    this,
                    "Password must be more than 7 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            return Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT).show()
        }
    }

    fun isLettersOrDigits(chars: String): Boolean {
        return chars.filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
            .length == chars.length
    }

    fun updateUser(name: String, email: String, password: String, image: String? = null) {

        db.collection("users")
            .whereEqualTo("username", userUsername).get().addOnSuccessListener {
                user.updatePassword(userPassword)
                user.updateEmail(userEmail)
                user.updateProfile(UserProfileChangeRequest.Builder()
                    .setDisplayName(userUsername)
                    .build()
                )
            }
    }
}