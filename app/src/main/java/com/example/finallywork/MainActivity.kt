package com.example.finallywork

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.models.Role
import com.example.finallywork.models.User
import com.example.finallywork.ui.admin.AdminPanelActivity
import com.example.finallywork.ui.auth.LoginActivity
import com.example.finallywork.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            handleUser()
        }, 1000)
        setContentView(R.layout.activity_main)
    }

    private fun handleUser() {
        firebaseAuth.currentUser?.let { user ->
            User.getUser(
                authId = user.uid,
                onSuccess = {
                    handleUser(it)
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                })
        } ?: openAuth()
    }

    private fun handleUser(user: User) {
        when (user.role) {
            Role.USER -> openHome()
            Role.ADMIN -> openAdminPanel()
            else -> openHome()
        }
        finish()
    }

    private fun openAdminPanel() {
        val adminIntent = Intent(this, AdminPanelActivity::class.java)
        startActivity(adminIntent)
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun openAuth() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}