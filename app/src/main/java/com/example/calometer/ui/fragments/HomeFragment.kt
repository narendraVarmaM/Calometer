package com.example.calometer.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.calometer.R
import com.example.calometer.database.SessionManager
import com.example.calometer.databinding.FragmentHomeBinding
import com.example.calometer.ui.viewmodels.FoodDetailsViewModel
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding:FragmentHomeBinding

    lateinit var viewModel: FoodDetailsViewModel

    lateinit var userViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("home", "on create")
        val sessionManager = SessionManager(requireNotNull(this.activity).application)
        if(!sessionManager.checkLogin()){
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment,null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment,
                        true).build())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.show()

        // Inflate the layout for this fragment

        binding= FragmentHomeBinding.inflate(inflater,container,false)

        viewModel=ViewModelProvider(requireActivity()).get(FoodDetailsViewModel::class.java)

        userViewModel=ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)

        val calorieGoal=viewModel.sessionManager.getCalorieGoal()

        viewModel.apply {
            getFoodConsumed(MaterialDatePicker.todayInUtcMilliseconds()).observe(viewLifecycleOwner) {
                getTotalCaloriesConsumed(it)
            }

            totalCalories.observe(viewLifecycleOwner) { caloriesConsumed ->
                binding.apply {
                    tvCalorieGoal.text = calorieGoal.toString()
                    tvCalorieConsumed.text = caloriesConsumed.toString()
                    tvCalorieRemaining.text = (calorieGoal - caloriesConsumed).toString()
                }
            }
        }
        userViewModel.apply {
            getUserDetails().observe(viewLifecycleOwner){
                it?.let{ user ->
                    val carbohydrate= (user.calorieGoal*0.50/4).roundToInt()
                    val protein = (user.calorieGoal*0.20/4).roundToInt()
                    val fat = (user.calorieGoal*0.30/9).roundToInt()
                    val splitUp="Carbs ${carbohydrate}g / Fat ${fat}g / Protein ${protein}g"
                    binding.apply {
                        tvWeightValue.text=user.goalWeight.toString()
                        tvGoalSelected.text=user.goal
                        tvCalorieGoals.text=user.calorieGoal.toString()
                        tvMacroSplit.text=splitUp
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRdiFragment())
        }

        return binding.root
    }
}