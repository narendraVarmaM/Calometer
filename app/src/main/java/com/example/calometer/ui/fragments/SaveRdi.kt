package com.example.calometer.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calometer.databinding.FragmentSaveRdiBinding
import com.example.calometer.models.enums.Nutrients
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.example.calometer.util.Util
import kotlin.math.roundToInt


class SaveRdi : Fragment() {

    private lateinit var binding:FragmentSaveRdiBinding

    private lateinit var viewModel: UserProfileViewModel

    private lateinit var args: SaveRdiArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentSaveRdiBinding.inflate(layoutInflater,container,false)

        viewModel=ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)

        args=SaveRdiArgs.fromBundle(requireArguments())

        var calorieGoal=args.user.calorieGoal

        binding.apply {
            etCalorieGoal.setText(args.user.calorieGoal.toString())

            btnSave.setOnClickListener {
                args.user.calorieGoal=calorieGoal
                viewModel.updateUser(args.user)
            }
            btnRecalculate.setOnClickListener {
                findNavController().navigateUp()
            }

            etCalorieGoal.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val result: String = etCalorieGoal.text.toString().trimStart('0')
                    if (result == "") {
                        etCalorieGoal.setText(args.user.calorieGoal.toString())
                    } else {
                        etCalorieGoal.setText(result)
                        calorieGoal = result.toInt()
                    }
                    binding.btnSave.isEnabled = true
                }
            }
            val textWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    btnSave.isEnabled = false
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val result: String = etCalorieGoal.text.toString().trimStart('0')
                    if (result.isEmpty()) {
                        btnSave.isEnabled = false
                    } else {
                        btnSave.isEnabled = true
                        calorieGoal=result.toInt()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            }

            etCalorieGoal.addTextChangedListener(textWatcher)

            etCalorieGoal.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        etCalorieGoal.clearFocus()
                        val inputMethodManager =
                            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(etCalorieGoal.windowToken, 0)
                        true
                    }
                    else -> false
                }
            }
        }

        viewModel.apply {
            navigateToHomeFragment.observe(viewLifecycleOwner) {
                it?.let {
                    showToastMessage()
                    findNavController().navigate(SaveRdiDirections.actionSaveRdiToHomeFragment())
                    doneNavigatingToHomeFragment()
                }
            }
        }

        return binding.root
    }

    private fun showToastMessage() {
        Toast.makeText(requireContext(), "Updated goals successfully", Toast.LENGTH_SHORT).show()
    }
}