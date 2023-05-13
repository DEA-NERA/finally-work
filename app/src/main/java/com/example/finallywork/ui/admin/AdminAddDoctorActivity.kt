package com.example.finallywork.ui.admin

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityAdminAddDoctorBinding
import com.example.finallywork.databinding.ActivityAdminPanelBinding
import com.example.finallywork.models.Appointment
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

class AdminAddDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddDoctorBinding
    private val listOfSpecializationsCheckBoxes = ArrayList<CheckBox>()
    private lateinit var doctor: Doctor
    private val REQUEST_IMAGE_GET = 1

    private val storageReference: StorageReference by lazy { Firebase.storage.reference }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            uriContent?.let {
                val metadata = storageMetadata {
                    contentType = "image/jpg"
                }
                val reference =
                    storageReference.child("files/userImage/" + firebaseAuth.currentUser?.uid)
                reference.putFile(
                    Uri.parse(result.uriContent.toString()), metadata
                )
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            Toast.makeText(this, result.error?.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminAddDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listOfSpecializationsCheckBoxes.add(binding.checkBox)
        listOfSpecializationsCheckBoxes.add(binding.checkBox2)
        listOfSpecializationsCheckBoxes.add(binding.checkBox3)
        listOfSpecializationsCheckBoxes.add(binding.checkBox4)
        listOfSpecializationsCheckBoxes.add(binding.checkBox5)
        binding.AvatarImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)

            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
        intent?.let {
            it.extras?.let { extras ->
                val doctorId = extras.getString("doctor")
                doctorId?.let {
                    Doctor.getById(
                        doctorId,
                        onSuccess = { result ->
                            binding.AddDoctorButton.visibility = View.GONE
                            binding.EditDoctorButton.visibility = View.VISIBLE
                            doctor = result
                            binding.NameDoctorEditText.setText(doctor.firstName)
                            binding.SurNameDoctorEditText.setText(doctor.lastName)
                            binding.RoomDoctorEditText.setText(doctor.roomNumber)
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
                            listOfSpecializationsCheckBoxes.map { checkBox ->
                                doctor.specializations.map { specialization ->
                                    if (checkBox.text == specialization) {
                                        checkBox.isChecked = true
                                    }
                                }
                            }
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                        })
                } ?: {
                    binding.AddDoctorButton.visibility = View.VISIBLE
                    binding.EditDoctorButton.visibility = View.GONE
                }
            } ?: {
                binding.AddDoctorButton.visibility = View.VISIBLE
                binding.EditDoctorButton.visibility = View.GONE
            }
        } ?: {
            binding.AddDoctorButton.visibility = View.VISIBLE
            binding.EditDoctorButton.visibility = View.GONE
        }


        binding.AddDoctorButton.setOnClickListener {
            getDoctor()?.addToDataBase(
                onSuccess = {
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                })
        }

        binding.EditDoctorButton.setOnClickListener {
            getDoctor(false)?.edit(
                onSuccess = {
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                })
        }

        binding.DBirthDoctorEditText.setOnClickListener {
            openDatePicker(binding.DBirthDoctorEditText)
        }
        binding.DateStartWorkDoctorEditText.setOnClickListener {
            openDatePicker(binding.DateStartWorkDoctorEditText)
        }

        binding.infoToolbar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data != null) {
            try {

                val imageUri: Uri? = data.data
                binding.AvatarImageView.setImageURI(imageUri)
                if (imageUri != null) {
                    openCropActivity(imageUri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    private fun getDoctor(isCreate: Boolean? = true): Doctor? {
        val dateOfBirth =
            SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).parse(binding.DBirthDoctorEditText.text.toString())
        val dateStartWork =
            SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).parse(binding.DateStartWorkDoctorEditText.text.toString())
        val specializations = ArrayList<String>()

        listOfSpecializationsCheckBoxes.map {
            if (it.isChecked)
                specializations.add(it.text.toString())
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
                var id = UUID.randomUUID().toString()
                if (isCreate == false && doctor != null)
                    id = doctor.id
                return Doctor(
                    id = id,
                    lastName = binding.SurNameDoctorEditText.text.toString(),
                    firstName = binding.NameDoctorEditText.text.toString(),
                    roomNumber = binding.RoomDoctorEditText.text.toString(),
                    dateOfBirth = birthDate,
                    dateStartWork = startWorkDate,
                    rating = 5.0,
                    specializations = specializations,
                    appointments = appointments
                )
            } ?: return null
        } ?: return null
    }


    private fun openCropActivity(uri: Uri) {
        cropImageLauncher.launch(
            CropImageContractOptions(
                uri,
                CropImageOptions(
                    cropShape = CropImageView.CropShape.OVAL,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    fixAspectRatio = true,
                    outputCompressFormat = Bitmap.CompressFormat.JPEG,
                    outputCompressQuality = 20,
                    progressBarColor = getColor(R.color.main_green),
                )
            )
        )
    }

}