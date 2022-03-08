package com.example.calometer.ui.fragments

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.databinding.FragmentEditWeightDetailsBinding
import com.example.calometer.ui.viewmodels.WeightDetailsViewModel
import com.example.calometer.util.DecimalDigitsInputFilter
import com.example.calometer.util.Util
import com.example.calometer.util.Util.Companion.convertUTCToDate
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class EditWeightDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEditWeightDetailsBinding
    private lateinit var viewModel:WeightDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentEditWeightDetailsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity()).get(WeightDetailsViewModel::class.java)
        binding.apply {
            ivRemoveIcon.setOnClickListener {
                decreaseWeight()
            }
            ivAddIcon.setOnClickListener {
                increaseWeight()
            }
            tvDate.text = convertUTCToDate(viewModel.selectedWeightDetail.value!!.date)
            tvWeightValue.setText(viewModel.selectedWeightDetail.value!!.weight.toString())
            tvWeightValue.filters = (arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 1)))
            tvWeightValue.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    ivAddIcon.isEnabled = false
                    ivAddIcon.alpha = 0.5f
                    ivRemoveIcon.isEnabled = false
                    ivRemoveIcon.alpha = 0.5f
                }
            }
            ivCheckIcon.setOnClickListener {
                val bmi=calculateBMI(tvWeightValue.text.toString().toDouble(), viewModel.height.value!!.toInt())
                if(bmi in 18.0..38.0){
                    updateWeightDetails()
                }
                else{
                    showDialogToAddWeight(bmi)
                }
                viewModel.setUpdatedWeight(tvWeightValue.text.toString().toDouble())
            }
            ivCloseIcon.setOnClickListener {
                findNavController().navigateUp()
            }
            btnDelete.setOnClickListener {
                viewModel.deleteWeightDetails()
                findNavController().navigate(EditWeightDetailsFragmentDirections.actionEditWeightDetailsFragmentToWeightFragment())
            }
        }
        viewModel.getWeightDetails().observe(viewLifecycleOwner) {
            it?.let { resultList ->
                if (resultList.size == 1) {
                    binding.btnDelete.visibility = View.INVISIBLE
                } else {
                    binding.btnDelete.visibility = View.VISIBLE
                }

            }
        }
        return binding.root
    }

    private fun calculateBMI(weight: Double, height: Int): Double {
        return (weight/height/height)*10000
    }

    private fun updateWeightDetails() {
        binding.apply {
            viewModel.setUpdatedWeight(tvWeightValue.text.toString().toDouble())
            findNavController().navigate(EditWeightDetailsFragmentDirections.actionEditWeightDetailsFragmentToWeightFragment())
        }
    }

    private fun showDialogToAddWeight(bmi:Double) {
        if(bmi<18) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please Note:")
                .setMessage("\nBased on your height, your current weight is very low and you should seek the advice of a medical doctor before beginning any diet\n\nAre you sure you wish to proceed\n")
                .setPositiveButton("Ok") { _, _ ->
                    updateWeightDetails()
                }
                .setNeutralButton("Cancel") { _, _ ->

                }.show()
        }
        else{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please Note:")
                .setMessage("\nBased on your height, your current weight is very high and you should seek the advice of a medical doctor before beginning any diet\n\nAre you sure you wish to proceed\n")
                .setPositiveButton("Ok") { _, _ ->
                    updateWeightDetails()
                }
                .setNeutralButton("Cancel") { _, _ ->

                }.show()
        }
    }


    private fun increaseWeight() {
        var currentWeight:Double=binding.tvWeightValue.text.toString().toDouble()
        if(currentWeight<999.9){
            currentWeight += 0.1
            val result= Util.formatDecimal(currentWeight)
            binding.tvWeightValue.setText(result)
        }
    }

    private fun decreaseWeight() {
        var currentWeight:Double=binding.tvWeightValue.text.toString().toDouble()
        if(currentWeight>0.0){
            currentWeight -= 0.1
            val result= Util.formatDecimal(currentWeight)
            binding.tvWeightValue.setText(result)
        }
    }
}