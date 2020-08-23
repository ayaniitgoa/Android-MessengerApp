package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button_register.setOnClickListener {
        val email = email_edittext_registeration.text.toString()
        val password = password_edittext_registeration.text.toString()

        Log.d("MainActivity",  " Email is $email" )
        Log.d("MainActivity",  " Password is $password" )

        }

        already_have_account_textview.setOnClickListener {
            Log.d("MainActivity", "Show login page.")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}