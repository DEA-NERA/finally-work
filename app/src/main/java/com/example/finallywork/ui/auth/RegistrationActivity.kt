package com.example.finallywork.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.NextButton.setOnClickListener {
            binding.EmailEditText2.text?.let { email ->
                binding.PaswordEditText2.text?.let { password ->
                    onNextButtonClick(email.toString(), password.toString())
                }
            }
        }

        binding.Login.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

    }

    private fun onNextButtonClick(email: String, password: String) {
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(
                this@RegistrationActivity,
                "Електронна пошта і пароль не можуть бути пустими",
                Toast.LENGTH_LONG
            ).show()
        } else if (email.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "Email can`t be empty", Toast.LENGTH_LONG)
                .show()
        } else if (password.isEmpty() || password.length < 8) {
            Toast.makeText(
                this@RegistrationActivity,
                "Пароль не можу бути пустим і мати менше 8 символів",
                Toast.LENGTH_LONG
            ).show()
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val getStartedIntent = Intent(this, FillInfoActivity::class.java)
                        startActivity(getStartedIntent)
                    } else {
                        Toast.makeText(
                            this@RegistrationActivity,
                            task.exception?.localizedMessage?.trimIndent(),
                            Toast.LENGTH_LONG
                        ).show()
                        firebaseAuth.currentUser?.delete()?.addOnFailureListener { exception ->
                            Toast.makeText(
                                this@RegistrationActivity,
                                exception.localizedMessage?.trimIndent(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }

}