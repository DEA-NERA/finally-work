package com.example.finallywork.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.finallywork.databinding.ActivityEditProfileBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.User
import com.google.firebase.auth.FirebaseAuth
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

class EditProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var user: User
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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
                    user.photoUrl = reference.putFile(
                        Uri.parse(uri.toString()), metadata
                    ).await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                    user.photoUrl?.let { url ->
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
        setContentView(R.layout.activity_edit_profile)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AvatarImageView.setOnClickListener {
            checkPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE
            )
        }

        firebaseAuth.currentUser?.uid?.let {
            User.getUser(
                authId = it,
                onSuccess = { result ->
                    user = result
                    user.photoUrl?.let { url ->
                        if (url == "null") {
                            binding.AvatarImageView.setImageResource(R.drawable.photo_default)
                        } else
                            Picasso.get().load(url).into(binding.AvatarImageView)
                    } ?: binding.AvatarImageView.setImageResource(R.drawable.photo_default)
                    binding.NameEditText.setText(user.firstName)
                    binding.SurNameEditText.setText(user.lastName)
                    user.phoneNumber?.let { phone ->
                        binding.PhoneNumberEditText.setText(phone)
                    }
                    binding.DBirthEditText.setText(
                        SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).format(
                            user.dateOfBirth
                        )
                    )
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                })
        }
        binding.EditButton.setOnClickListener {
            getUser()?.edit(
                onSuccess = {
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                })
        }

        binding.DBirthEditText.setOnClickListener {
            openDatePicker(binding.DBirthEditText)
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

    private fun getUser(): User? {
        val dateOfBirth =
            SimpleDateFormat(Doctor.DATE_FORMAT_PATTERN).parse(binding.DBirthEditText.text.toString())

        dateOfBirth?.let { birthDate ->
            return User(
                authId = user.authId,
                lastName = binding.SurNameEditText.text.toString(),
                firstName = binding.NameEditText.text.toString(),
                phoneNumber = binding.PhoneNumberEditText.text.toString(),
                dateOfBirth = birthDate,
                photoUrl = user.photoUrl
            )
        } ?: return null
    }
}