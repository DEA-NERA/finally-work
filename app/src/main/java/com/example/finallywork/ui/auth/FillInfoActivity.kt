package com.example.finallywork.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityFillInfoBinding
import com.example.finallywork.models.User
import com.example.finallywork.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class FillInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillInfoBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_info)

        binding = ActivityFillInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.infoToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.BDateEditText.setOnClickListener {
            openDatePicker()
        }

        binding.RegisterButton.setOnClickListener {
            firebaseAuth.currentUser?.let {
                val dateOfBirth =
                    SimpleDateFormat(User.DATE_FORMAT_PATTERN).parse(binding.BDateEditText.text.toString())
                dateOfBirth?.let { dateOfBirth ->
                    val user = User(
                        authId = it.uid,
                        lastName = binding.SurnameEditText.text.toString(),
                        firstName = binding.NameEditText.text.toString(),
                        dateOfBirth = dateOfBirth
                    )

                    user.addToDataBase(
                        onSuccess = {
                            openHomeActivity()
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                        })
                }
            }
        }
    }

    private fun openHomeActivity() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    private fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(this, R.style.DatePicker)
        val calendar = Calendar.getInstance()
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ -> }

        if (binding.BDateEditText.text.toString().isNotEmpty()) {
            val date = LocalDate.parse(
                binding.BDateEditText.text.toString(),
                DateTimeFormatter.ofPattern(User.DATE_FORMAT_PATTERN)
                    .withZone(ZoneId.systemDefault())
            )
            datePickerDialog.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
        }
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setOnClickListener {
            calendar.set(
                datePickerDialog.datePicker.year,
                datePickerDialog.datePicker.month,
                datePickerDialog.datePicker.dayOfMonth
            )
            binding.BDateEditText.setText(SimpleDateFormat(User.DATE_FORMAT_PATTERN).format(calendar.time))
            datePickerDialog.hide()
        }
    }
}
