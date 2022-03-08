package com.example.calometer.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.models.entity.User
import com.example.calometer.databinding.FragmentUpdateProfileBinding
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar

class UpdateProfileFragment : Fragment() {

    lateinit var binding: FragmentUpdateProfileBinding
    lateinit var viewModel:UserProfileViewModel
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentUpdateProfileBinding.inflate(layoutInflater,container,false)

        viewModel=ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)

        binding.apply {
            btnUpdate.setOnClickListener {
                if(validateAllFields()){
                    currentUser.password=etPassword.text.toString()
                    currentUser.name=etName.text.toString()
                    viewModel.updateUserPassword(currentUser)
                }
            }
        }

        viewModel.apply {
            getUserDetails().observe(viewLifecycleOwner) { user ->
                this@UpdateProfileFragment.currentUser = user.copy()
                if (user != null) {
                    binding.apply {
                        tilName.editText?.setText(user.name)
                        tilMobile.editText?.setText(user.mobileNo.toString())
                    }
                }

            }
            navigateToProfileFragment.observe(viewLifecycleOwner) {
                it?.let {
                    showSnackBarWithMessage()
                    findNavController().navigate(UpdateProfileFragmentDirections.actionUpdateProfileFragmentToUserProfileFragment())
                    doneNavigatingToProfileFragment()
                }
            }
        }

        return binding.root
    }

    private fun showSnackBarWithMessage() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Password Updated Successfully :)",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateName() = with(binding.tilName) {
        when {
            editText?.text.toString().isEmpty() -> {
                error = "Field cannot be empty"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateAllFields() :Boolean{
        return validateName() && validateMobileNo() && validatePassword() && validateConfirmPassword()
    }

    private fun validateMobileNo(): Boolean = with(binding.tilMobile) {
        val whiteSpace = Regex("(?=\\S+$)")
        when {
            editText?.text.toString().isEmpty() -> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString().length != 10 -> {
                error = "Field must contain exactly 10 values"
                false
            }
            editText?.text.toString().matches(whiteSpace) -> {
                error = "White Spaces are not allowed"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validatePassword() = with(binding.tilPassword) {
        when {
            editText?.text.toString().isEmpty() -> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString().length < 5 -> {
                error = "Password too weak"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateConfirmPassword() = with(binding.tilConfirmPassword) {
        when {
            editText?.text.toString().isEmpty() -> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString() != binding.tilPassword.editText?.text.toString() -> {
                error = "Password does not match"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}