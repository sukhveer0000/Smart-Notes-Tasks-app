package com.example.nexupnotes.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexupnotes.MainActivity
import com.example.nexupnotes.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val user = FirebaseAuth.getInstance().currentUser?.uid

        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}