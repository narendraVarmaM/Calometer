package com.example.calometer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calometer.MyApplication
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.models.entity.FoodData
import com.example.calometer.repository.FoodRepository
import com.example.calometer.database.SessionManager
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    private val application: MyApplication,
    private val foodRepository: FoodRepository
): ViewModel(){

    val sessionManager=SessionManager(application)
    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    private var _foodItemSelectedId:Int=0
    val foodItemSelectedId
        get() = _foodItemSelectedId

    private var _dateSelected = MutableLiveData<Long>()
    val dateSelected
        get() = _dateSelected

    private var _searchResult: MutableLiveData<List<FoodData>> = MutableLiveData()
    val searchResult
        get() = _searchResult

    init{
        getFoodConsumed(MaterialDatePicker.todayInUtcMilliseconds())
        _dateSelected.value= MaterialDatePicker.todayInUtcMilliseconds()
    }

    fun setDateSelected(date: Long){
        _dateSelected.value=date
    }
    fun setFoodItemId(id:Int){
        _foodItemSelectedId=id
    }

    private var _foodConsumed = MutableLiveData<List<FoodConsumed>>()
    val foodConsumed
        get() = _foodConsumed

    private var _foodData = MutableLiveData<List<FoodData>>()
    val foodData
        get()=_foodData
    private var _totalCalories=MutableLiveData<Int>()
    val totalCalories
        get() = _totalCalories

    fun getFood(date: Long)=viewModelScope.launch { getFoodConsumed(date) }

    fun getFoodConsumed(date:Long) = foodRepository.getFoodConsumed(date, sessionManager.getUserId())

    fun insertFood(foodConsumed: FoodConsumed)=viewModelScope.launch { foodRepository.insertFoodConsumed(foodConsumed) }
    fun doneShowingSearchResults() {
        _searchResult.value = null
    }

    fun getSearchResult(searchQuery: String) = foodRepository.search("%$searchQuery%")

    fun getFoodDetailsById(id: Int):LiveData<FoodData> = foodRepository.getFoodDetailsById(id)

        fun updateFoodConsumed(foodConsumed: FoodConsumed)=viewModelScope.launch {
            foodRepository.updateFoodConsumed(foodConsumed)
        }
    fun deleteFood(foodConsumed: FoodConsumed)=viewModelScope.launch {
        foodRepository.deleteFoodConsumed(foodConsumed)
    }

    fun getUserDetails()=foodRepository.getUser(sessionManager.getUserId())

    fun getTotalCaloriesConsumed(listFood: List<FoodConsumed>?) {
        listFood?.let{
            var sum=0
            it.forEach { food -> sum+=food.calories }
            _totalCalories.value=sum
        }?:run{_totalCalories.value=0}
    }

    fun getFoodDetailsById(listFood: List<FoodConsumed>?)=viewModelScope.launch {
        listFood?.let{
            val foodId:List<Int> = it.map{foodConsumed -> foodConsumed.foodId }
            _foodData.value=foodRepository.getFoodDetailsById(foodId)
        }?: run{_foodData.value=null}
    }

}

