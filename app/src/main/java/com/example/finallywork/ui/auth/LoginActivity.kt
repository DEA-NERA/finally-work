package com.example.finallywork.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityLoginBinding
import com.example.finallywork.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registrateBtn.setOnClickListener {
            val createAccIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(createAccIntent)
            finish()
        }

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        firebaseAuth.signInWithEmailAndPassword(
            binding.EmailEditText.text.toString(),
            binding.PaswordEditText.text.toString()
        )
            .addOnSuccessListener {
                openHome()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun openHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

}





