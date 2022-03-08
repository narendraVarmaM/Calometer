package com.example.calometer.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calometer.adapter.NutritionalInfoAdapter
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.databinding.FragmentSaveFoodItemBinding
import com.example.calometer.models.enums.Nutrients
import com.example.calometer.ui.viewmodels.FoodDetailsViewModel
import com.example.calometer.util.Util
import kotlin.math.roundToInt


class SaveFoodItemFragment : Fragment() {

    lateinit var binding: FragmentSaveFoodItemBinding
    lateinit var viewModel: FoodDetailsViewModel
    lateinit var arguments: SaveFoodItemFragmentArgs
    private lateinit var nutritionalInfoAdapter: NutritionalInfoAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        binding = FragmentSaveFoodItemBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(FoodDetailsViewModel::class.java)


        arguments = SaveFoodItemFragmentArgs.fromBundle(requireArguments())

        var nutritionalValue: List<Double?>
        arguments.foodData.apply {
            nutritionalValue = listOf(
                this.calories,
                this.fat,
                this.protein,
                this.carbohydrate,
                this.sugars,
                this.fiber,
                this.cholesterol,
                this.saturatedFats,
                this.calcium,
                this.iron,
                this.potassium,
                this.magnesium,
                this.sodium,
                this.monoUnsaturatedFat,
                this.polyUnsaturatedFat
            )
        }

        nutritionalInfoAdapter = NutritionalInfoAdapter(
            Nutrients.values().map { it.headingValue },
            Util.getNutritionalValuesWithUnits(nutritionalValue)
        )

        val foodConsumed = FoodConsumed(
            0,
            arguments.dateSelected,
            arguments.mealType.name,
            1,
            arguments.foodData.id,
            viewModel.sessionManager.getUserId(),
            arguments.foodData.calories.roundToInt(),
            arguments.foodData.foodName
        )

        binding.apply {

            tvFoodName.text = arguments.foodData.foodName

            if (arguments.foodData.servingDescription != null) {
                tvFoodServing.text = arguments.foodData.servingDescription
            } else {
                tvFoodServing.text = "100g"
            }

            etQuantity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val result: String = etQuantity.text.toString().trimStart('0')
                    val newList = nutritionalValue.toMutableList()
                    if (result == "") {
                        etQuantity.setText("1")
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index] = nutritionalValue[index]?.times(1)
                        }
                    } else {
                        etQuantity.setText(result)
                        foodConsumed.quantity = result.toInt()
                        foodConsumed.calories =
                            (foodConsumed.quantity * arguments.foodData.calories).roundToInt()
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index] = nutritionalValue[index]?.times(result.toInt())
                        }
                    }
                    setUpRecyclerView(
                        Nutrients.values().map { it.headingValue },
                        Util.getNutritionalValuesWithUnits(newList)
                    )
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
                    val newList = nutritionalValue.toMutableList()
                    val result: String = etQuantity.text.toString().trimStart('0')
                    if (result.isEmpty()) {
                        btnSave.isEnabled = false
                    } else {
                        btnSave.isEnabled = true
                        foodConsumed.quantity = result.toInt()
                        foodConsumed.calories =
                            (foodConsumed.quantity * arguments.foodData.calories).roundToInt()
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index] = nutritionalValue[index]?.times(result.toInt())
                        }
                        setUpRecyclerView(
                            Nutrients.values().map { it.headingValue },
                            Util.getNutritionalValuesWithUnits(newList)
                        )
                    }

                }

                override fun afterTextChanged(s: Editable) {}
            }

            etQuantity.addTextChangedListener(textWatcher)

            etQuantity.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        etQuantity.clearFocus()
                        val inputMethodManager =
                            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(etQuantity.windowToken, 0)
                        true
                    }
                    else -> false
                }
            }

            btnSave.setOnClickListener {
                saveDetails(foodConsumed)
            }

            rvNutrientValues.apply {
                adapter = nutritionalInfoAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

        return binding.root
    }

    private fun saveDetails(foodConsumed: FoodConsumed) {

        viewModel.insertFood(foodConsumed)
        findNavController().navigate(SaveFoodItemFragmentDirections.actionSaveFoodItemFragmentToDiaryFragment2())

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView(nutrients: List<String>, values: List<String>) {
        nutritionalInfoAdapter = NutritionalInfoAdapter(nutrients, values)
        nutritionalInfoAdapter.notifyDataSetChanged()
        binding.rvNutrientValues.apply {
            adapter = nutritionalInfoAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}

//            etQuantity.addTextChangedListener(object: TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    var result:String=s.toString().trimStart('0')
//                    val newList= nutritionalValue.toMutableList()
//                    if(result==""){
//                        etQuantity.setText("1")
//                    }
//                    else{
//                        etQuantity.setText(result)
//                        nutritionalValue.forEachIndexed { index, d ->
//                            newList[index]=nutritionalValue[index]?.times(result.toInt())
//                        }
//                    }
//                    nutritionalInfoAdapter=NutritionalInfoAdapter(Nutrients.values().map{it.headingValue}, Util.getNutritionalValuesWithUnits(newList))
//                    nutritionalInfoAdapter.notifyDataSetChanged()
//
//                    binding.rvNutrientValues.apply {
//                        adapter=nutritionalInfoAdapter
//                        layoutManager=LinearLayoutManager(activity)
//                    }
//                    btnSave.isEnabled=true
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    btnSave.isEnabled=false
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    btnSave.isEnabled=false
//                }
//            }
//            )
