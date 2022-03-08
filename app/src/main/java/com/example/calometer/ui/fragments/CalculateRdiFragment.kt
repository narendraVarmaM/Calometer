package com.example.calometer.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.R
import com.example.calometer.models.entity.User
import com.example.calometer.databinding.FragmentCalculateRdiBinding
import com.example.calometer.models.enums.ActivityLevel
import com.example.calometer.models.enums.Gender
import com.example.calometer.models.enums.Goal
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.example.calometer.util.Util
import com.google.android.material.datepicker.MaterialDatePicker


class CalculateRdiFragment : Fragment() {

    private lateinit var binding: FragmentCalculateRdiBinding

    private lateinit var viewModel:UserProfileViewModel

    private lateinit var currentUser: User



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentCalculateRdiBinding.inflate(inflater, container, false)

        viewModel=ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)


        val goals:Array<String> = Goal.values().map { it.displayValue }.toTypedArray()
        val goalAdapter= ArrayAdapter(requireContext(), R.layout.drop_down_item, goals)
        val gender= Gender.values()
        val genderAdapter= ArrayAdapter(requireContext(),R.layout.drop_down_item,gender)
        val activityLevels= ActivityLevel.values().map { it.displayValue }.toTypedArray()
        val activityAdapter= ArrayAdapter(requireContext(),R.layout.drop_down_item,activityLevels)

        val activityLevelMap=mutableMapOf<String,ActivityLevel>()

        ActivityLevel.values().forEach { activityLevelMap[it.displayValue] = it  }

        val goalsMap= mutableMapOf<String,Goal>()

        Goal.values().forEach { goalsMap[it.displayValue] = it }

        binding.apply {

            actGoal.setAdapter(goalAdapter)

            actGender.setAdapter(genderAdapter)

            actActivityLevel.setAdapter(activityAdapter)

            btnCalculate.setOnClickListener {
                val validation=validateAllFields()
                if(validation) {
                    val weight = etWeight.text.toString().toDouble()
                    val height = etHeight.text.toString().toInt()
                    val age = etAge.text.toString().toInt()
                    val genderInput=actGender.text.toString()
                    val activityLevel: ActivityLevel? = activityLevelMap[actActivityLevel.text.toString()]
                    val goal = goalsMap[actGoal.text.toString()]
                    val calorieGoal = calculateCalorieGoal(weight, height, age, genderInput,activityLevel,goal)
                    Log.i("calorie goal", "$calorieGoal")
                    currentUser.height=height
                    currentUser.age=age
                    currentUser.gender=genderInput
                    currentUser.activityLevel=activityLevel!!.displayValue
                    currentUser.goal=goal!!.displayValue
                    currentUser.calorieGoal=calorieGoal
                    findNavController().navigate(CalculateRdiFragmentDirections.actionCalculateRdiFragmentToSaveRdi(currentUser))
                }
            }

        }

        viewModel.apply {
            getUserDetails().observe(viewLifecycleOwner, { user ->
                this@CalculateRdiFragment.currentUser = user.copy()
                if (user != null) {
                    binding.apply {
                        tvTodayDate.text =
                            Util.convertUTCToDate(MaterialDatePicker.todayInUtcMilliseconds())
                        etAge.setText(user.age.toString())
                        etWeight.setText(user.startWeight.toString())
                        etHeight.setText(user.height.toString())
                        actGender.setText(user.gender)
                        actGoal.setText(user.goal, false)
                        actActivityLevel.setText(user.activityLevel, false)
                    }
                }
            }
            )
        }

        return binding.root
    }

    private fun validateAllFields() :Boolean{
        return  validateGoal() && validateActivityLevel() && validateGender()&&validateAge()&&validateHeight()&&validateWeight()
    }

    private fun calculateCalorieGoal(
        weight: Double,
        height: Int,
        age: Int,
        gender: String,
        activityLevel: ActivityLevel?,
        goal: Goal?
    ): Int {
        binding.apply {
            var calorieGoal = 0
            val bmr = if (gender == "Male") {
                10 * weight + 6.25 * height - 5 * age + 5
            } else {
                10 * weight + 6.25 * height - 5 * age - 161
            }
            Log.i("bmr","$bmr")
            activityLevel?.let {
                calorieGoal = (activityLevel.factor * bmr).toInt()
            }
            Log.i("rdi","maintain $calorieGoal")
            goal?.let {
                calorieGoal += goal.factor
            }
            return calorieGoal
        }
    }

    private fun validateGoal() = with(binding.tilGoal){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateGender() = with(binding.tilGender){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateActivityLevel() = with(binding.tilActivityLevel){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateHeight() = with(binding.tilHeight){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString() == "0" ->{
                error = "Height cannot be zero"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateWeight() = with(binding.tilWeight){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString() == "0" ->{
                error = "Weight cannot be zero"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun validateAge() = with(binding.tilAge){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString() == "0" ->{
                error = "Age cannot be zero"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }
}