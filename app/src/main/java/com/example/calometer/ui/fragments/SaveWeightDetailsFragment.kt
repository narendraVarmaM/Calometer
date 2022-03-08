package com.example.calometer.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.calometer.models.entity.User
import com.example.calometer.databinding.FragmentSaveWeightDetailsBinding
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.example.calometer.ui.viewmodels.WeightDetailsViewModel
import com.example.calometer.util.DecimalDigitsInputFilter
import com.example.calometer.util.Util.Companion.formatDecimal
import com.example.calometer.util.Util.Companion.roundOffDecimal
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveWeightDetailsFragment : Fragment() {

    lateinit var binding:FragmentSaveWeightDetailsBinding
    lateinit var viewModel:WeightDetailsViewModel
    private lateinit var currentUser: User
    private lateinit var userViewModel: UserProfileViewModel
    private lateinit var args: SaveWeightDetailsFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.hide()

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentSaveWeightDetailsBinding.inflate(inflater,container,false)

        viewModel=ViewModelProvider(this).get(WeightDetailsViewModel::class.java)
        userViewModel=ViewModelProvider(this).get(UserProfileViewModel::class.java)

        args= SaveWeightDetailsFragmentArgs.fromBundle(requireArguments())

        viewModel.apply {
            dateSelected.observe(viewLifecycleOwner
            ) {
                it?.let {
                    binding.tvAutoComplete.setText(it)
                }
            }
        }

        userViewModel.apply {
            getUserDetails().observe(viewLifecycleOwner) { user ->
                this@SaveWeightDetailsFragment.currentUser = user.copy()
            }
            navigateToWeightFragment.observe(viewLifecycleOwner) {
                it?.let {
                    findNavController().safeNavigate(SaveWeightDetailsFragmentDirections.actionSaveWeightDetailsFragmentToWeightFragment())
                    doneNavigatingToWeightFragment()
                }
            }
        }

        when(args.source){
            2-> {
                binding.tilDate.visibility=View.INVISIBLE
                binding.tvText.text = "Enter Goal Weight"
            }
            1->{
                binding.tilDate.visibility=View.VISIBLE
                binding.tvText.text = "Enter Start weight"

            }
            0->{
                binding.tilDate.visibility=View.VISIBLE
                binding.tvText.text = "Weigh In"

            }
        }

        binding.apply {
            binding.tvWeightValue.setText(args.weight)
            tilDate.setEndIconOnClickListener {
                viewModel.datePicker.show(parentFragmentManager, "tag")

                viewModel.datePicker.addOnPositiveButtonClickListener  {
                    viewModel.setDateSelectedUTC(viewModel.datePicker.selection!!.toLong())
                    if(it==MaterialDatePicker.todayInUtcMilliseconds()){
                        viewModel.setDateSelected("Today")
                    }
                    else{
                        viewModel.setDateSelected(viewModel.datePicker.headerText)
                    }

                }
            }
            ivRemoveIcon.setOnClickListener {
                decreaseWeight()
            }
            ivAddIcon.setOnClickListener {
                increaseWeight()
            }

            tvWeightValue.filters=(arrayOf<InputFilter>(DecimalDigitsInputFilter(3,1)))
            tvWeightValue.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    ivAddIcon.isEnabled=false
                    ivAddIcon.alpha=0.5f
                    ivRemoveIcon.isEnabled=false
                    ivRemoveIcon.alpha=0.5f
                }
            }
            ivCheckIcon.setOnClickListener {
                val bmi=calculateBMI(tvWeightValue.text.toString().toDouble(), viewModel.height.value!!.toInt())
                if(bmi in 18.0..38.0){
                    when(args.source){
                        0->addWeightDetails()
                        1->addWeightDetails()
                        2->updateGoalWeight()
                    }
                }
                else{
                    showDialogToAddWeight(bmi)
                }
            }
            ivCloseIcon.setOnClickListener {
                findNavController().navigateUp()
            }

        }
        return binding.root
    }

    private fun updateGoalWeight() {
        currentUser.goalWeight=binding.tvWeightValue.text.toString().toDouble()
        userViewModel.updateUserGoalWeight(currentUser)
    }

    private fun calculateBMI(weight: Double, height: Int): Double {
        return (weight/height/height)*10000
    }

    private fun addWeightDetails() {
        binding.apply {
            viewModel.insertWeightDetails(tvWeightValue.text.toString().toDouble())
            findNavController().navigate(SaveWeightDetailsFragmentDirections.actionSaveWeightDetailsFragmentToWeightFragment())
        }
    }

    private fun showDialogToAddWeight(bmi:Double) {
        if(bmi<18) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please Note:")
                .setMessage("\nBased on your height, your current weight is very low and you should seek the advice of a medical doctor before beginning any diet\n\nAre you sure you wish to proceed\n")
                .setPositiveButton("Ok") { _, _ ->
                    addWeightDetails()
                }
                .setNeutralButton("Cancel") { _, _ ->

                }.show()
        }
        else{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please Note:")
                .setMessage("\nBased on your height, your current weight is very high and you should seek the advice of a medical doctor before beginning any diet\n\nAre you sure you wish to proceed\n")
                .setPositiveButton("Ok") { _, _ ->
                    addWeightDetails()
                }
                .setNeutralButton("Cancel") { _, _ ->

                }.show()
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    private fun increaseWeight() {
        var currentWeight:Double=binding.tvWeightValue.text.toString().toDouble()
        if(currentWeight<999.9){
            currentWeight += 0.1
            val result=formatDecimal(currentWeight)
            binding.tvWeightValue.setText(result)
        }
    }

    private fun decreaseWeight() {
        var currentWeight:Double=binding.tvWeightValue.text.toString().toDouble()
        if(currentWeight>0.0){
            currentWeight -= 0.1
            val result=formatDecimal(currentWeight)
            binding.tvWeightValue.setText(result)
        }
    }


}


