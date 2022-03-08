package com.example.calometer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.databinding.FragmentRdiBinding
import com.example.calometer.ui.viewmodels.UserProfileViewModel


class RdiFragment : Fragment() {

    lateinit var binding: FragmentRdiBinding

    lateinit var viewModel:UserProfileViewModel




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentRdiBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)



        binding.apply {
            btnRecalculate.setOnClickListener {
                navigateToRdiFragment()
            }
        }

        viewModel.apply {
            getUserDetails().observe(viewLifecycleOwner, { user ->
                binding.apply {
                    tvCalorieGoal.text = user.calorieGoal.toString()
                }
            })

        }


        return binding.root
    }

    private fun navigateToRdiFragment(){
        findNavController().navigate(RdiFragmentDirections.actionRdiFragmentToCalculateRdiFragment())
    }

}