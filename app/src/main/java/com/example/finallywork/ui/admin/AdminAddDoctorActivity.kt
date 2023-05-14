package com.example.finallywork.ui.admin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityAdminAddDoctorBinding
import com.example.finallywork.models.Appointment
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

class AdminAddDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddDoctorBinding
    private val listOfSpecializationsCheckBoxes = ArrayList<CheckBox>()
    private var doctor: Doctor? = null

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }

    private val storageReference: StorageReference by lazy { Firebase.storage.reference }
    private val changeProfileImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                data?.let {
                    it.data?.let { uri -> openCropActivity(uri) }
                }
            }
        }
    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            uriContent?.let { uri ->
                val metadata = storageMetadata {
                    contentType = "image/jpg"
                }
                val reference =
                    storageReference.child("files/userImage/" + UUID.randomUUID().toString())
                lifecycleScope.launch {
                    doctor?.photoUrl = reference.putFile(
                        Uri.parse(uri.toString()), metadata
                    ).await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                    doctor?.photoUrl?.let { url ->
                        if (url == "null") {
                            binding.AvatarImageView.setImageResource(R.drawable.photo_default)
                        } else
                            Picasso.get().load(url).into(binding.AvatarImageView)
                    } ?: binding.AvatarImageView.setImageResource(R.drawable.photo_default)

                }
            }
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
            checkPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE
            )
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
                            result.photoUrl?.let { url ->
                                if (url == "null") {
                                    binding.AvatarImageView.setImageResource(R.drawable.photo_default)
                                } else {
                                    Picasso.get().load(url).into(binding.AvatarImageView)
                                }
                            } ?: binding.AvatarImageView.setImageResource(R.drawable.photo_default)
                            binding.NameDoctorEditText.setText(result.firstName)
                            binding.SurNameDoctorEditText.setText(result.lastName)
                            binding.RoomDoctorEditText.setText(result.roomNumber)
                            binding.DBirthDoctorEditText.setText(
                                SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(
                                    result.dateOfBirth
                                )
                            )
                            binding.DateStartWorkDoctorEditText.setText(
                                SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(
                                    result.dateStartWork
                                )
                            )
                            listOfSpecializationsCheckBoxes.map { checkBox ->
                                result.specializations.map { specialization ->
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                changeProfileImageLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(
                    this,
                    "Надайте доступ до сховища в налаштуваннях додатку",
                    Toast.LENGTH_LONG
                )
                    .show()
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
        if (editText == binding.DBirthDoctorEditText) {
            calendar.add(Calendar.YEAR, -18)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        }
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
                doctor?.let {
                    if (isCreate == false)
                        id = it.id
                }
                var photo = "null"
                doctor?.photoUrl?.let {
                    photo = it
                }
                return Doctor(
                    id = id,
                    lastName = binding.SurNameDoctorEditText.text.toString(),
                    firstName = binding.NameDoctorEditText.text.toString(),
                    roomNumber = binding.RoomDoctorEditText.text.toString(),
                    dateOfBirth = birthDate,
                    dateStartWork = startWorkDate,
                    rating = 5.0,
                    specializations = specializations,
                    appointments = appointments,
                    photoUrl = photo
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

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeProfileImageLauncher.launch(galleryIntent)
        }
    }

}