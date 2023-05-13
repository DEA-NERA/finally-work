package com.example.finallywork

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityFilterBinding


class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    private val listOfSpecializationsCheckBoxes = ArrayList<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filterToolbar.setNavigationOnClickListener {
            finish()
        }

        listOfSpecializationsCheckBoxes.add(binding.checkBoxDepress)
        listOfSpecializationsCheckBoxes.add(binding.checkBoxEmotion)
        listOfSpecializationsCheckBoxes.add(binding.checkBoxLoveself)
        listOfSpecializationsCheckBoxes.add(binding.checkBoxPanic)
        listOfSpecializationsCheckBoxes.add(binding.checkBoxSuicide)
        val specializations = ArrayList<String>()

        binding.buttonFilterOk.setOnClickListener {
            listOfSpecializationsCheckBoxes.map {
                if (it.isChecked)
                    specializations.add(it.text.toString())
            }
            val data = Intent()
            data.putExtra("filterList", specializations)
            setResult(RESULT_OK, data)
            finish()
        }

    }

}