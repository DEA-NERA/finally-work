package com.example.finallywork.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityAdminPanelBinding

class AdminPanelActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAdminPanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
    }
}