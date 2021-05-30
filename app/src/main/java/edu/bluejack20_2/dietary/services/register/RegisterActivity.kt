package edu.bluejack20_2.dietary.services.register

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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack20_2.dietary.CirleImageView
import edu.bluejack20_2.dietary.R
import edu.bluejack20_2.dietary.services.login.LoginActivity
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream


class RegisterActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var storageRef: StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var userPic: CirleImageView
    private lateinit var userPicURI: Uri
    private lateinit var userEmail: String
    private lateinit var userName: String
    private lateinit var userPassword: String
    private lateinit var userUsername: String
    private var bitmap: Bitmap? = null

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userPic = findViewById(R.id.img_pick_btn)
        findViewById<Button>(R.id.firstBtn).setOnClickListener {
            firstValidation(it)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_CODE
        )
    }

    fun changeImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(
                    permissions,
                    PERMISSION_CODE
                );
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                        .show()
                }
            }
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

    fun firstValidation(view: View) {
        val name: TextInputEditText = findViewById(R.id.nameText)
        val email: TextInputEditText = findViewById(R.id.emailText)
        val password: TextInputEditText = findViewById(R.id.passwordText)
        val username: TextInputEditText = findViewById(R.id.usernameText)
        userName = name.text.toString().trim()
        userEmail = email.text.toString().trim()
        userPassword = password.text.toString()
        userUsername = username.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$"
        if (userName == "") {
            findViewById<TextView>(R.id.nameNull).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.nameNull).visibility = View.INVISIBLE
        }
        if (userUsername == "") {
            findViewById<TextView>(R.id.usernameNull).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.usernameNull).visibility = View.INVISIBLE
        }
        if (userEmail == "") {
            findViewById<TextView>(R.id.emailNull).visibility = View.VISIBLE
        } else {
            if (!userEmail.matches(emailPattern.toRegex())) {
                findViewById<TextView>(R.id.emailNull).text = getString(R.string.email_invalid)
                findViewById<TextView>(R.id.emailNull).visibility = View.VISIBLE
            } else {
                findViewById<TextView>(R.id.emailNull).visibility = View.INVISIBLE
            }
        }
        if (userPassword == "") {
            findViewById<TextView>(R.id.passwordNull).visibility = View.VISIBLE
            return
        } else {
            if (userPassword.length < 8) {
                findViewById<TextView>(R.id.passwordNull).text = getString(R.string.password_length)
                findViewById<TextView>(R.id.passwordNull).visibility = View.VISIBLE
                return
            }
            if (!userPassword.matches(passPattern.toRegex())) {
                findViewById<TextView>(R.id.passwordNull).text =
                    getString(R.string.password_alphanumeric)
                findViewById<TextView>(R.id.passwordNull).visibility = View.VISIBLE
                return
            }
            if (userPassword.matches(passPattern.toRegex()) && userPassword.length >= 8) {
                findViewById<TextView>(R.id.passwordNull).visibility = View.INVISIBLE
            }
        }
        if (userEmail.matches(emailPattern.toRegex())) {
            if (userPassword.matches(passPattern.toRegex()) && userPassword.length >= 8) {
                writeNewUser(userName, userUsername, userEmail, userPassword, bitmap.toString())
                findViewById<TextView>(R.id.passwordNull).visibility = View.INVISIBLE
            }
        } else {
            findViewById<TextView>(R.id.emailNull).text = getString(R.string.email_invalid)
            findViewById<TextView>(R.id.emailNull).visibility = View.VISIBLE
            return
        }
    }

    fun isLetters(chars: String): Boolean {
        return chars.filter { it in 'A'..'Z' || it in 'a'..'z' }
            .length == chars.length
    }

    fun isDigits(chars: String): Boolean {
        return chars.filter { it in '0'..'9' }
            .length == chars.length
    }


    fun writeNewUser(
        name: String,
        username: String,
        email: String,
        password: String,
        image: String? = null
    ) {

        db.collection("users")
            .whereEqualTo("username", userUsername).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection("users").whereEqualTo("email", userEmail).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                val user: MutableMap<String, Any> = HashMap()
                                user["username"] = username
                                user["password"] = password
                                user["email"] = email
                                user["name"] = name
                                user["photoURL"] = "https://miro.medium.com/max/720/1*W35QUSvGpcLuxPo3SRTH4w.png"
                                if (bitmap != null) {
                                    val stream = ByteArrayOutputStream()
                                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                    val imageRef = stream.toByteArray()
                                    storageRef.child("user/${userUsername}").putBytes(imageRef)
                                        .addOnSuccessListener {

                                            storageRef.child("user/${userUsername}").downloadUrl.addOnSuccessListener {

                                                user["photoURL"] = it.toString()
                                                db.collection("users")
                                                    .add(user)
                                                    .addOnSuccessListener { documentReference ->
                                                        Log.d(
                                                            "ok",
                                                            "DocumentSnapshot added with ID: " + documentReference.id
                                                        )
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("breakfastHour", 6)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("lunchHour", 12)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("dinnerHour", 7)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("snackHour", 3)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("breakfastMinute", 1)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("lunchMinute", 1)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("dinnerMinute", 1)
                                                        db.collection("users")
                                                            .document(documentReference.id)
                                                            .update("snackMinute", 1)
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            "ok",
                                                            "Error adding document",
                                                            e
                                                        )
                                                    }
                                            }
                                        }
                                } else {
                                    db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d(
                                                "ok",
                                                "DocumentSnapshot added with ID: " + documentReference.id
                                            )
                                            db.collection("users").document(documentReference.id)
                                                .update("breakfastHour", 6)
                                            db.collection("users").document(documentReference.id)
                                                .update("lunchHour", 12)
                                            db.collection("users").document(documentReference.id)
                                                .update("dinnerHour", 7)
                                            db.collection("users").document(documentReference.id)
                                                .update("snackHour", 3)
                                            db.collection("users").document(documentReference.id)
                                                .update("breakfastMinute", 1)
                                            db.collection("users").document(documentReference.id)
                                                .update("lunchMinute", 1)
                                            db.collection("users").document(documentReference.id)
                                                .update("dinnerMinute", 1)
                                            db.collection("users").document(documentReference.id)
                                                .update("snackMinute", 1)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "ok",
                                                "Error adding document",
                                                e
                                            )
                                        }
                                }
                                startActivity(Intent(this, LoginActivity::class.java))

                            } else {
                                findViewById<TextView>(R.id.passwordNull).text =
                                    getString(R.string.email_taken)
                                findViewById<TextView>(R.id.passwordNull).visibility = View.VISIBLE
                            }
                        }
                } else {
                    findViewById<TextView>(R.id.passwordNull).text =
                        getString(R.string.username_taken)
                    findViewById<TextView>(R.id.passwordNull).visibility = View.VISIBLE

                }
            }
    }

    fun gotoLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}