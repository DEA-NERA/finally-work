package com.example.finallywork.ui.home

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finallywork.R
import com.example.finallywork.databinding.FragmentHomeBinding
import com.example.finallywork.models.User
import com.example.finallywork.models.UserAppointment
import com.example.finallywork.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


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
        binding.editButton.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getUser()
    }

    private fun getUser() {
        firebaseAuth.currentUser?.let {
            User.getUser(
                authId = it.uid,
                onSuccess = { user ->
                    binding.userDateBirthTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.userNumberPhoneTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                    binding.userNameTextView.text = user.lastName + " " + user.firstName
                    binding.userEmailTextView.text = it.email
                    binding.DataBirthTextView.text =
                        SimpleDateFormat(UserAppointment.DATE_FORMAT_PATTERN).format(user.dateOfBirth?.time)
                    binding.NumPhoneTextView.text = user.phoneNumber
                    user.photoUrl?.let { url ->
                        if (url == "null") {
                            binding.Avatar.setImageResource(R.drawable.photo_default)
                        } else
                            Picasso.get().load(url).into(binding.Avatar)
                    } ?: binding.Avatar.setImageResource(R.drawable.photo_default)
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), exception, Toast.LENGTH_LONG).show()
                })
        }
    }

}




