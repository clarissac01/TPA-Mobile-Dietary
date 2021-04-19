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
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


class RegisterActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var storageRef: StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var userPic: CirleImageView
    private lateinit var userPicURI: Uri
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userUsername: String
    private var bitmap: Bitmap? = null

    companion object{
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

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun changeImage(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else{
                pickImageFromGallery();
            }
        }
        else{
            pickImageFromGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            userPicURI = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, userPicURI)
            userPic.setImageBitmap(bitmap!!)
        }
    }

    fun firstValidation(view: View) {
        Log.wtf("wtf", "wtf")
        val email:TextInputEditText = findViewById(R.id.emailText)
        val password:TextInputEditText = findViewById(R.id.passwordText)
        val username:TextInputEditText = findViewById(R.id.usernameText)
        userEmail = email.text.toString().trim()
        userPassword = password.text.toString()
        userUsername = username.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passPattern = "[a-zA-Z0-9]"
        if(userEmail == "" ){
            return Toast.makeText(this, "You must input Email!", Toast.LENGTH_SHORT).show()
        }
        if(userPassword == ""){
            return Toast.makeText(this, "You must input Password!", Toast.LENGTH_SHORT).show()
        }
        if(userUsername == ""){
            return Toast.makeText(this, "You must input Username!", Toast.LENGTH_SHORT).show()
        }
        if(userEmail.matches(emailPattern.toRegex())){
            if(isLettersOrDigits(userPassword) && userPassword.length >= 8 ){
                writeNewUser(userUsername, userEmail, userPassword, bitmap.toString())
            }
            else if(!userPassword.matches(passPattern.toRegex())){
                return Toast.makeText(this, "Password must be alphanumeric!", Toast.LENGTH_SHORT).show()
            }
            else{
                return Toast.makeText(this, "Password must be more than 7 characters!", Toast.LENGTH_SHORT).show()
            }
        }else{
            return Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT).show()
        }
    }

    fun isLettersOrDigits(chars: String): Boolean {
        return chars.filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
                .length == chars.length
    }

    fun writeNewUser(name: String, email: String, password: String, image: String?=null) {

        db.collection("users")
            .whereEqualTo("username",  userUsername).whereEqualTo("email", userEmail).get().addOnSuccessListener {
                if(it.isEmpty){
                    val user: MutableMap<String, Any> = HashMap()
                    user["username"] = name
                    user["password"] = password
                    user["email"] = email
                    if(bitmap!=null){
                        val stream = ByteArrayOutputStream()
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val imageRef = stream.toByteArray()
                        storageRef.child("user/${userUsername}").putBytes(imageRef).addOnSuccessListener{

                            storageRef.child("user/${userUsername}").downloadUrl.addOnSuccessListener {

                                user["photoURL"] = it.toString()
                                db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener { documentReference -> Log.d("ok", "DocumentSnapshot added with ID: " + documentReference.id) }
                                        .addOnFailureListener { e -> Log.w("ok", "Error adding document", e) }
                            }
                        }
                    }else{
                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener { documentReference -> Log.d("ok", "DocumentSnapshot added with ID: " + documentReference.id) }
                                .addOnFailureListener { e -> Log.w("ok", "Error adding document", e) }
                    }
                    startActivity(Intent(this, LoginActivity::class.java))
                }else{
                    Toast.makeText(this, "This username or email is taken!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun gotoLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}