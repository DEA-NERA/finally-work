package com.example.finallywork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.finallywork.ui.auth.LoginActivity
import com.example.finallywork.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            handleUser()
            finish()
        }, 3000)
        setContentView(R.layout.activity_main)
    }

    private fun handleUser() {
        firebaseAuth.currentUser?.let {
            openHome()
        } ?: openAuth()
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun openAuth() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}