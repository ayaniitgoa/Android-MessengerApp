package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textview.setOnClickListener {
            Log.d("RegisterActivity", "Show login page.")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        profile_img_register.setOnClickListener {
            Log.d("RegisterActivity", "Select Profile Picture")
            Toast.makeText(this, "Select profile photo from gallery!", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity", "Photo Selected")
            Toast.makeText(this, "Photo Selected!", Toast.LENGTH_SHORT).show()

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profile_img_register.setBackgroundDrawable(bitmapDrawable)

            select_photo_register_view.setImageBitmap(bitmap)
            profile_img_register.alpha = 0f
        }
    }

    private fun performRegister() {
        val email = email_edittext_registeration.text.toString()
        val password = password_edittext_registeration.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please make sure you have filled email or password!", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("RegisterActivity",  " Email is $email" )
        Log.d("RegisterActivity",  " Password is $password" )

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity", "Created User with id ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create user. ${it.message}", Toast.LENGTH_LONG).show()
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDB(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDB(profileImgUrl: String){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_edittext_registeration.text.toString(), profileImgUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally saved the user to firebaseDB")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}

class User(val uid: String, val username: String, val profileImgUrl: String){
    constructor(): this("", "", "")
}