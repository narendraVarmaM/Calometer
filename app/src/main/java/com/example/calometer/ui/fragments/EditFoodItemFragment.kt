package com.example.calometer.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
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
import com.example.calometer.models.entity.FoodData
import com.example.calometer.databinding.FragmentEditFoodItemBinding
import com.example.calometer.models.enums.Nutrients
import com.example.calometer.ui.viewmodels.FoodDetailsViewModel
import com.example.calometer.util.Util
import android.text.Editable
import kotlin.math.roundToInt


class EditFoodItemFragment : Fragment() {

    private lateinit var binding: FragmentEditFoodItemBinding
    private lateinit var viewModel: FoodDetailsViewModel
    private lateinit var nutritionalInfoAdapter: NutritionalInfoAdapter
    private lateinit var arguments:EditFoodItemFragmentArgs
    private lateinit var foodData: FoodData
    private lateinit var nutritionalValue:List<Double?>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentEditFoodItemBinding.inflate(inflater,container,false)

        viewModel=ViewModelProvider(requireActivity()).get(FoodDetailsViewModel::class.java)

        arguments=EditFoodItemFragmentArgs.fromBundle(requireArguments())




        setUpRecyclerView(emptyList(), emptyList())



        viewModel.getFoodDetailsById(arguments.foodConsumed.foodId).observe(viewLifecycleOwner,{

            it?.let{foodDataResult->
                foodData=foodDataResult
                setUpNutritionalInformation(foodData)
            }
        })

        binding.apply {

            etQuantity.setOnFocusChangeListener { _, hasFocus ->
                if(!hasFocus){
                    val result:String=etQuantity.text.toString().trimStart('0')
                    val newList= nutritionalValue.toMutableList()
                    if(result==""){
                        etQuantity.setText(arguments.foodConsumed.quantity.toString())
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index]=nutritionalValue[index]?.times(arguments.foodConsumed.quantity)
                        }
                    }
                    else{
                        etQuantity.setText(result)
                        arguments.foodConsumed.quantity=result.toInt()
                        arguments.foodConsumed.calories=(arguments.foodConsumed.quantity * foodData.calories).roundToInt()
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index]=nutritionalValue[index]?.times(result.toInt())
                        }
                    }
                    setUpRecyclerView(Nutrients.values().map{it.headingValue}, Util.getNutritionalValuesWithUnits(newList))
                    btnSave.isEnabled=true
                }
            }

            val textWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    btnSave.isEnabled=false
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val newList= nutritionalValue.toMutableList()
                    val result:String=etQuantity.text.toString().trimStart('0')
                    if( result==arguments.foodConsumed.quantity.toString() || result.isEmpty()){
                        btnSave.isEnabled = false
                    }
                    else{
                        btnSave.isEnabled=true
                        arguments.foodConsumed.quantity=result.toInt()
                        arguments.foodConsumed.calories=(arguments.foodConsumed.quantity * foodData.calories).roundToInt()
                        nutritionalValue.forEachIndexed { index, _ ->
                            newList[index]=nutritionalValue[index]?.times(result.toInt())
                        }
                        setUpRecyclerView(Nutrients.values().map{it.headingValue}, Util.getNutritionalValuesWithUnits(newList))
                    }

                }
                override fun afterTextChanged(s: Editable) {}
            }

//            etQuantity.setOnClickListener{
//                Log.i("on click", "1")
//                automaticChanged=true
//                btnSave.isEnabled=true
//            }
            etQuantity.addTextChangedListener(textWatcher)



            etQuantity.setOnEditorActionListener { _, actionId, _ ->
                when(actionId){
                    EditorInfo.IME_ACTION_DONE -> {
                        etQuantity.clearFocus()
                        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(etQuantity.windowToken, 0)
                        true
                    }
                    else -> false
                }
            }

            btnSave.setOnClickListener {
                saveDetails(arguments.foodConsumed)
            }

            btnDelete.setOnClickListener {
                deleteDetails(arguments.foodConsumed)
            }
        }
        return binding.root
    }

    private fun deleteDetails(foodConsumed: FoodConsumed) {
        viewModel.deleteFood(foodConsumed)
        findNavController().navigate(EditFoodItemFragmentDirections.actionEditFoodItemFragmentToDiaryFragment())

    }

    @SuppressLint("SetTextI18n")
    private fun setUpNutritionalInformation(foodData: FoodData) {

        foodData.apply {
            nutritionalValue=listOf(this.calories, this.fat, this.protein, this.carbohydrate, this.sugars, this.fiber,this.cholesterol, this.saturatedFats, this.calcium, this.iron, this.potassium,
                this.magnesium, this.sodium, this.monoUnsaturatedFat, this.polyUnsaturatedFat)
        }

        val newList= nutritionalValue.toMutableList()


        nutritionalValue.forEachIndexed { index, _ ->
            newList[index]=nutritionalValue[index]?.times(arguments.foodConsumed.quantity)
        }

        setUpRecyclerView(Nutrients.values().map{it.headingValue}, Util.getNutritionalValuesWithUnits(newList))

        binding.apply {
            tvFoodName.text=foodData.foodName

            if(foodData.servingDescription!=null){
                tvFoodServing.text=foodData.servingDescription
            }
            else{
                tvFoodServing.text="100g"
            }

            etQuantity.setText(arguments.foodConsumed.quantity.toString())
        }

    }

    private fun saveDetails(foodConsumed: FoodConsumed) {

        viewModel.insertFood(foodConsumed)
        findNavController().navigate(EditFoodItemFragmentDirections.actionEditFoodItemFragmentToDiaryFragment())

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView(nutrients:List<String>, values:List<String>){
        nutritionalInfoAdapter=NutritionalInfoAdapter(nutrients, values)
        nutritionalInfoAdapter.notifyDataSetChanged()
        binding.rvNutrientValues.apply {
            adapter=nutritionalInfoAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }

}