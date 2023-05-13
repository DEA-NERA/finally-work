package com.example.finallywork.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finallywork.FilterActivity
import com.example.finallywork.databinding.FragmentDoctorsBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.ui.adapters.DoctorAdapter
import com.example.finallywork.ui.appointment.CreateAppointmentActivity


class DoctorsFragment : Fragment() {

    private lateinit var binding: FragmentDoctorsBinding

    private lateinit var doctorAdapter: DoctorAdapter
    private val FILTER_REQUEST_CODE = 0x9988

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorsBinding.inflate(
            inflater, container,
            false
        )
        doctorAdapter = DoctorAdapter(requireContext())
        doctorAdapter.onAddAppointment = {
            val appointmentIntent = Intent(requireContext(), CreateAppointmentActivity::class.java)
            appointmentIntent.putExtra("doctor", it.id)
            startActivity(appointmentIntent)
        }
        binding.adminDoctorsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = doctorAdapter
        }
        getAllDoctors()

        binding.SearchAdminEditText.addTextChangedListener {
            if (binding.SearchAdminEditText.text.isBlank()) {
                getAllDoctors()
            } else {
                Doctor.getByName(
                    value = binding.SearchAdminEditText.text.toString(),
                    onSuccess = {
                        doctorAdapter.doctorList = it
                    },
                    onFailure = { exception ->
                        exception.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }

        binding.FilterBtn.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), FilterActivity::class.java),
                FILTER_REQUEST_CODE
            )
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringArrayListExtra("filterList")
                Doctor.getAll(
                    onSuccess = {
                        val filterList = it.filter { doctor ->
                            doctor.specializations.containsAll(result as ArrayList<String>)
                        }
                        doctorAdapter.doctorList = filterList
                        if (filterList.isEmpty()) {
                            binding.doctorsIsEmpty.visibility = View.VISIBLE
                        } else {
                            binding.doctorsIsEmpty.visibility = View.GONE
                        }
                    },
                    onFailure = { exception ->
                        exception.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        getAllDoctors()
    }


    private fun getAllDoctors() {
        Doctor.getAll(
            onSuccess = {
                doctorAdapter.doctorList = it
                if (it.size == 0) {
                    binding.doctorsIsEmpty.visibility = View.VISIBLE
                } else {
                    binding.doctorsIsEmpty.visibility = View.GONE
                }
            }, onFailure = { exception ->
                exception.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            })
    }

}