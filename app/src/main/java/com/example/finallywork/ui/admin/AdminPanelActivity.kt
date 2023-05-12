package com.example.finallywork.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finallywork.R
import com.example.finallywork.databinding.ActivityAdminPanelBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.ui.adapters.DoctorAdapter


class AdminPanelActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAdminPanelBinding
    private val doctorAdapter: DoctorAdapter by lazy { DoctorAdapter(this, true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AddBtn.setOnClickListener {
            val adminIntent = Intent(this, AdminAddDoctorActivity::class.java)
            startActivity(adminIntent)
        }

        doctorAdapter.onDeleteClick = { doctor ->
            val dialog = Builder(this, R.style.AlertDialogTheme)
            dialog.setTitle("Ви впевнені, що хочете видалити цього лікаря?")
            dialog.setPositiveButton("Так") { _, _ ->
                doctor.delete(
                    onSuccess = {
                        getAllDoctors()
                    },
                    onFailure = { exception ->
                        exception.let {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                    })
            }
            dialog.setNegativeButton(
                "Відміна"
            ) { dialog, _ ->
                dialog.cancel()
            }
            dialog.show()
        }
        doctorAdapter.onEditClick = { doctor ->
            val adminIntent = Intent(this, AdminAddDoctorActivity::class.java)
            adminIntent.putExtra("doctor", doctor.id)
            startActivity(adminIntent)
        }

        binding.adminDoctorsList.apply {
            layoutManager =
                LinearLayoutManager(this@AdminPanelActivity, LinearLayoutManager.VERTICAL, false)
            adapter = doctorAdapter
        }

        binding.SearchAdminEditText.addTextChangedListener {
            if (binding.SearchAdminEditText.text.isBlank()) {
                getAllDoctors()
            } else {
                Doctor.getByName(
                    value = binding.SearchAdminEditText.text.toString(),
                    onSuccess = {
                        doctorAdapter.doctorList = it
                    }, onFailure = { exception ->
                        exception.let {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllDoctors()
    }

    private fun getAllDoctors() {
        Doctor.getAll(
            onSuccess = {
                doctorAdapter.doctorList = it
            }, onFailure = { exception ->
                exception.let {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            })
    }
}