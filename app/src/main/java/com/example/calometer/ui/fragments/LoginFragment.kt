package com.example.calometer.ui.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.R
import com.example.calometer.databinding.FragmentLoginBinding
import com.example.calometer.ui.viewmodels.AuthenticationViewModel
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity()).get(AuthenticationViewModel::class.java)
        binding.apply {
            tvSignUp.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
            }

            when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    appIcon.setImageResource(R.drawable.splash_background)
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    appIcon.setImageResource(R.drawable.splash_backround_night)
                }
            }
            btnLogin.setOnClickListener {
                viewModel.apply {
                    if(validateAllFields()) {
                        validateUser(
                            tilMobile.editText?.text.toString().toLong(),
                            tilPassword.editText?.text.toString()
                        )
                    }
                    showToastMessage.observe(viewLifecycleOwner) {
                        it?.let {
                            showToastMessage(it)
                            doneShowingToastMessage()
                        }
                    }
                    navigateToHomeFragment.observe(viewLifecycleOwner) {
                        it?.let {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                            doneNavigatingToHomeFragment()
                        }
                    }
                }
            }
        }

        return binding.root
    }
    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun validateAllFields():Boolean{
        var fields: List<TextInputLayout>
        binding.apply {
            fields = listOf(tilMobile, tilPassword)
        }
        return !fields.map { it.validateField() }.contains(false)
    }

    private fun TextInputLayout.validateField() = when {
        editText?.text.toString().isEmpty() -> {
            error = "Field cannot be empty"
            false
        }
        editText?.text.toString().length<5 ->{
            error = "Invalid values!"
            false
        }
        else -> {
            error = null
            true
        }
    }

}