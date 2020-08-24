package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        login_button.setOnClickListener {
            loginUser()
        }

        dont_have_account_textview.setOnClickListener {
            finish()
        }
    }

    private fun loginUser(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please make sure you have filled email or password!", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("Login", "Email is $email and password is $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Login", "Logged in with id ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to login. ${it.message}", Toast.LENGTH_LONG).show()
                Log.d("Login", "Failed to login: ${it.message}")
            }
    }
}