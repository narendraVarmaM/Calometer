package com.example.calometer.ui.fragments

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.R
import com.example.calometer.models.entity.User
import com.example.calometer.databinding.FragmentSignUpBinding
import com.example.calometer.models.enums.ActivityLevel
import com.example.calometer.models.enums.Gender
import com.example.calometer.models.enums.Goal
import com.example.calometer.ui.viewmodels.AuthenticationViewModel
import com.example.calometer.util.DecimalDigitsInputFilter

class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUpBinding

    private lateinit var viewModel: AuthenticationViewModel

    private lateinit var activityLevelSelected:ActivityLevel

    private lateinit var goalSelected:Goal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentSignUpBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity()).get(AuthenticationViewModel::class.java)
        val goals:Array<String> = Goal.values().map { it.displayValue }.toTypedArray()
        val goalAdapter=ArrayAdapter(requireContext(), R.layout.drop_down_item, goals)
        val gender=Gender.values()
        val genderAdapter=ArrayAdapter(requireContext(),R.layout.drop_down_item,gender)
        val activityLevels=ActivityLevel.values().map { it.displayValue }.toTypedArray()
        val activityAdapter=ArrayAdapter(requireContext(),R.layout.drop_down_item,activityLevels)
        goalSelected=Goal.Maintain
        activityLevelSelected=ActivityLevel.Sedentary

        hideWeightChange()
        binding.apply {
            actGoal.setAdapter(goalAdapter)

            actGender.setAdapter(genderAdapter)

            actActivityLevel.setAdapter(activityAdapter)

            actGoal.setOnItemClickListener { _, _, position, _ ->
                when(position){
                    0->{
                        goalSelected=Goal.Loss
                        showWeightChange()
                    }
                    1->{
                        goalSelected=Goal.Maintain
                        hideWeightChange()
                    }
                    else->{
                        goalSelected=Goal.Gain
                        showWeightChange()
                    }
                }
            }

            actActivityLevel.setOnItemClickListener { _, _, position, _ ->
                when(position){
                    0->{
                        activityLevelSelected=ActivityLevel.Sedentary
                    }
                    1->{
                        activityLevelSelected=ActivityLevel.Low
                    }
                    2->{
                        activityLevelSelected=ActivityLevel.Medium
                    }
                    3->{
                        activityLevelSelected=ActivityLevel.High
                    }
                }
            }


            btnSignUp.setOnClickListener {
                val validation=validateAllFields()
                if(validation){
                    val weight = etWeight.text.toString().toDouble()
                    val height = etHeight.text.toString().toInt()
                    val age = etAge.text.toString().toInt()
                    val genderInput = actGender.text.toString()
                    val calorieGoal=calculateCalorieGoal(weight,height,age,genderInput)
                    Log.i("calorie goal","$calorieGoal")

                    if(tilWeightChange.visibility==View.VISIBLE) {

                        if (validateWeightChange()) {
                            val goalWeight = weight + when (goalSelected) {
                                Goal.Gain -> etWeightChange.text.toString().toDouble()
                                else -> -etWeightChange.text.toString().toDouble()
                            }
                            val user = User(
                                0,
                                etName.text.toString(),
                                etMobile.text.toString().toLong(),
                                etPassword.text.toString(),
                                age,
                                height,
                                weight,
                                goalSelected.displayValue,
                                goalWeight,
                                genderInput,
                                activityLevelSelected.displayValue,
                                calorieGoal
                            )
                            viewModel.insertUser(user, weight)
                        }
                    }
                    else{
                        val user= User(0,
                            etName.text.toString(),
                            etMobile.text.toString().toLong(),
                            etPassword.text.toString(),
                            age,
                            height,
                            weight,
                            goalSelected.displayValue,
                            weight,
                            genderInput,
                            activityLevelSelected.displayValue,
                            calorieGoal
                        )
                        viewModel.insertUser(user,weight)
                    }
                }
            }



            etWeight.filters=(arrayOf<InputFilter>(DecimalDigitsInputFilter(3,1)))
        }

        viewModel.apply {
            showToastMessage.observe(viewLifecycleOwner) {
                it?.let {
                    showToastMessage(it)
                    doneShowingToastMessage()
                }
            }
            navigateToLoginFragment.observe(viewLifecycleOwner) {
                it?.let {
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                    doneNavigatingToLoginFragment()
                }
            }
        }

        return binding.root
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun calculateCalorieGoal(weight: Double, height: Int, age: Int, gender: String): Int {
        binding.apply {
            var calorieGoal:Int
            val bmr = if (gender == "Male") {
                10 * weight + 6.25 * height - 5 * age + 5
            } else {
                10 * weight + 6.25 * height - 5 * age - 161
            }
            Log.i("bmr","$bmr")
            calorieGoal=(activityLevelSelected.factor*bmr).toInt()
            Log.i("rdi","maintain $calorieGoal")
            calorieGoal+=goalSelected.factor
            return calorieGoal
        }
    }
    private fun validateAllFields() :Boolean{
        validateName()
        validateMobileNo()
        validatePassword()
        validateConfirmPassword()
        validateGoal()
        validateActivityLevel()
        validateGender()
        validateAge()
        validateHeight()
        validateWeight()


        return validateName()&&validateMobileNo()&&validatePassword()&&validateConfirmPassword()&&validateGoal()&&validateActivityLevel()&&validateGender()&&validateAge()&&validateHeight()&&validateWeight()
    }

    private fun validateName() = with(binding.tilName) {
        when {
            editText?.text.toString().isEmpty() -> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString().contains("[0-9]".toRegex()) ->{
                error = "Field cannot contain numbers"
                false
            }
            else -> {
                error = null
                true
            }
        }
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
        //val passwordVal = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")
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

    private fun validateWeightChange() = with(binding.tilWeightChange){
        when{
            editText?.text.toString().isEmpty()-> {
                error = "Field cannot be empty"
                false
            }
            editText?.text.toString() == "0" ->{
                error = "Weight change cannot be zero"
                false
            }
            else -> {
                error = null
                true
            }
        }
    }
    private fun showWeightChange(){
        binding.apply {
            tilWeightChange.visibility=View.VISIBLE
        }
    }

    private fun hideWeightChange() {
        binding.apply {
            tilWeightChange.visibility=View.GONE
        }
    }
}