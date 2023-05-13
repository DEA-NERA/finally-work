package com.example.finallywork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finallywork.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filterToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}