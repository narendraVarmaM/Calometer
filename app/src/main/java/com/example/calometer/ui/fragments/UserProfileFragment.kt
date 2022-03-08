package com.example.calometer.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.models.entity.User
import com.example.calometer.databinding.FragmentUserProfileBinding
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    lateinit var binding:FragmentUserProfileBinding
    private lateinit var currentUser: User
    lateinit var viewModel:UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.show()

        binding= FragmentUserProfileBinding.inflate(layoutInflater,container,false)

        viewModel= ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)


        binding.apply {
            cvEditProfile.setOnClickListener {
                findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToUpdateProfileFragment())
            }
            cvCalculateRdi.setOnClickListener {
                navigateToRdiFragment()
            }
            btnLogOut.setOnClickListener {
                showAlertDialogToLogout()
            }

        }

        viewModel.apply {
            getUserDetails().observe(viewLifecycleOwner) { user ->
                this@UserProfileFragment.currentUser = user.copy()
                if (user != null) {
                    binding.apply {
                        tilMobile.editText?.setText(user.mobileNo.toString())
                        tilAge.editText?.setText(user.age.toString())
                        tilGender.editText?.setText(user.gender)
                        tvName.text = user.name

                    }
                }

            }
            navigateToProfileFragment.observe(viewLifecycleOwner) {
                it?.let {
                    showSnackBarWithMessage("Password Updated Successfully :)")
                    doneNavigatingToProfileFragment()
                }
            }
        }

        return binding.root
    }





    private fun navigateToRdiFragment(){
        findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToRdiFragment())
    }

    private fun logOut() {
        viewModel.logoutCurrentUser()
        showSnackBarWithMessage("Logged Out!")
        findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToHomeFragment())
    }

    private fun showAlertDialogToLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Alert!")
            .setMessage("CONFIRM LOGOUT? ")
            .setPositiveButton("Yes") { _, _ ->
                logOut()
            }
            .setNeutralButton("Cancel") { _, _ ->
                Toast.makeText(requireContext(), "Logout cancelled!", Toast.LENGTH_SHORT).show()
            }.show()
    }

    private fun showSnackBarWithMessage(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

}