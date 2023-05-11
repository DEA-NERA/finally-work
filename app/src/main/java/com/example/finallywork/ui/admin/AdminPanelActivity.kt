package com.example.finallywork.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityAdminPanelBinding

class AdminPanelActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAdminPanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AddBtn.setOnClickListener {
            val adminIntent = Intent(this, AdminAddDoctorActivity::class.java)
            startActivity(adminIntent)
        }
    }
}