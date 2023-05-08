package com.example.finallywork.ui.home

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finallywork.databinding.FragmentHomeBinding
import com.example.finallywork.models.User
import com.example.finallywork.ui.auth.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val createdAtTimestamp = Timestamp(Date())
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(createdAtTimestamp.toDate())
        firebaseAuth.currentUser?.let {
            User.getUser(
                authId = it.uid,
                onSuccess = { user ->
                    binding.userNameTextView.text = user.lastName + " " + user.firstName
                    binding.userNameTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.DataBirthTextView.text = formattedDate
                    binding.DataBirthTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.userNumberPhoneTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), exception, Toast.LENGTH_LONG).show()
                })
        }
        binding.ExitAccBtn.setOnClickListener {
            firebaseAuth.currentUser?.let {
                firebaseAuth.signOut()
                val exitIntent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(exitIntent)
                requireActivity().finish()
            } ?: run {
                Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_LONG).show()
            }
        }
        binding.DellAccBtn.setOnClickListener {
            User.delete(onSuccess = {
                val dellIntent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(dellIntent)
                requireActivity().finish()
            }, onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            })
        }





        return binding.root
    }


}




