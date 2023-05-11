package com.example.finallywork.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finallywork.databinding.FragmentDoctorsBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.ui.adapters.DoctorAdapter


class DoctorsFragment : Fragment() {

    private lateinit var binding: FragmentDoctorsBinding

    private val doctorAdapter: DoctorAdapter by lazy { DoctorAdapter(requireContext()) }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorsBinding.inflate(inflater, container, false)

        binding.adminDoctorsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }

        return binding.root
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
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            })
    }

}