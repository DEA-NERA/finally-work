package com.example.finallywork.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finallywork.databinding.FragmentMyAppointmentsBinding
import com.example.finallywork.models.UserAppointment
import com.example.finallywork.ui.adapters.AppointmentAdapter
import com.example.finallywork.ui.appointment.RateAppointmentActivity

class MyAppointmentsFragment : Fragment() {

    private lateinit var binding: FragmentMyAppointmentsBinding
    private lateinit var appointmentAdapter: AppointmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAppointmentsBinding.inflate(inflater, container, false)

        appointmentAdapter = AppointmentAdapter(requireContext())
        getAllAppointments()

        appointmentAdapter.onCancelClick = {
            it.delete(
                onSuccess = {
                    getAllAppointments()
                },
                onFailure = { exception ->
                    exception.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        appointmentAdapter.onRateClick = {
            val rateIntent = Intent(requireContext(), RateAppointmentActivity::class.java)
            rateIntent.putExtra("doctor", it.doctorId)
            rateIntent.putExtra("appointment", it.id)
            startActivity(rateIntent)
        }

        binding.appointmentsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = appointmentAdapter
        }
        return binding.root
    }

    private fun getAllAppointments() {
        UserAppointment.getAll(
            onSuccess = {
                appointmentAdapter.list = it
                if (it.size == 0) {
                    binding.appointmentsIsEmpty.visibility = View.VISIBLE
                } else {
                    binding.appointmentsIsEmpty.visibility = View.GONE
                }
            },
            onFailure = { exception ->
                exception.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

}




