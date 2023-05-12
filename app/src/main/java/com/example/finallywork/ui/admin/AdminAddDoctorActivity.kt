package com.example.finallywork.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityAdminAddDoctorBinding
import com.example.finallywork.models.Appointment
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.User
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

class AdminAddDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddDoctorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminAddDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val doctorId = intent.getStringExtra("doctor")
            doctorId?.let {
                Doctor.getById(
                    doctorId,
                    onSuccess = { doctor ->
                        binding.NameDoctorEditText.setText(doctor.firstName)
                        binding.SurNameDoctorEditText.setText(doctor.lastName)
                        binding.DBirthDoctorEditText.setText(
                            SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(
                                doctor.dateOfBirth
                            )
                        )
                        binding.DateStartWorkDoctorEditText.setText(
                            SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(
                                doctor.dateStartWork
                            )
                        )
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
        }

        binding.AddDoctorButton.setOnClickListener {
            val dateOfBirth =
                SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).parse(binding.DBirthDoctorEditText.text.toString())
            val dateStartWork =
                SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).parse(binding.DateStartWorkDoctorEditText.text.toString())
            val specializations = ArrayList<String>()
            if (binding.checkBox.isChecked) {
                specializations.add(binding.checkBox.text.toString())
            }
            if (binding.checkBox2.isChecked) {
                specializations.add(binding.checkBox2.text.toString())
            }
            if (binding.checkBox3.isChecked) {
                specializations.add(binding.checkBox3.text.toString())
            }
            if (binding.checkBox4.isChecked) {
                specializations.add(binding.checkBox4.text.toString())
            }
            if (binding.checkBox5.isChecked) {
                specializations.add(binding.checkBox5.text.toString())
            }

            val appointments = ArrayList<Appointment>()
            val c = Calendar.getInstance()
            for (i in 0..30) {
                c.set(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH),
                    8,
                    0,
                    0
                )
                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                    c.add(Calendar.DATE, 3)
                else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                    c.add(Calendar.DATE, 2)
                else
                    c.add(Calendar.DATE, 1)
                for (j in 0..4) {
                    c.add(Calendar.HOUR, 1)
                    val date = c.time
                    appointments.add(Appointment(isAvailable = true, date = date))
                }
            }

            dateOfBirth?.let { birthDate ->
                dateStartWork?.let { startWorkDate ->
                    val doctor = Doctor(
                        id = UUID.randomUUID().toString(),
                        lastName = binding.SurNameDoctorEditText.text.toString(),
                        firstName = binding.NameDoctorEditText.text.toString(),
                        dateOfBirth = birthDate,
                        dateStartWork = startWorkDate,
                        rating = 5.0,
                        specializations = specializations,
                        appointments = appointments
                    )
                    doctor.addToDataBase(onSuccess = {
                        finish()
                    }, onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
                }
            }
        }

        binding.DBirthDoctorEditText.setOnClickListener {
            openDatePicker(binding.DBirthDoctorEditText)
        }
        binding.DateStartWorkDoctorEditText.setOnClickListener {
            openDatePicker(binding.DateStartWorkDoctorEditText)
        }
    }

    private fun openDatePicker(editText: EditText) {
        val datePickerDialog = DatePickerDialog(this, R.style.DatePicker)
        val calendar = Calendar.getInstance()
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ -> }

        if (editText.text.toString().isNotEmpty()) {
            val date = LocalDate.parse(
                editText.text.toString(),
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
            editText.setText(SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(calendar.time))
            datePickerDialog.hide()
        }
    }
}