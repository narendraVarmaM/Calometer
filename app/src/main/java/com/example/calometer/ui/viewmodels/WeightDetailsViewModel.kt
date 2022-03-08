package com.example.calometer.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calometer.MyApplication
import com.example.calometer.repository.FoodRepository
import com.example.calometer.database.SessionManager
import com.example.calometer.models.entity.WeightDetails
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightDetailsViewModel @Inject constructor(
    private val application: MyApplication,
    private val foodRepository: FoodRepository
):ViewModel() {

    private val sessionManager=SessionManager(application)

    private var _height = MutableLiveData<Int>()
    val height
        get() = _height



    init{
        _height.value=sessionManager.getHeight()
    }

    private val constraintsBuilder =
        CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds()))

    val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

    private var _dateSelectedUTC = MutableLiveData<Long>()
    val dateSelectedUTC
        get() = _dateSelectedUTC

    private var _dateSelected =MutableLiveData<String>()
    val dateSelected
        get() = _dateSelected

    private var _selectedWeightDetail = MutableLiveData<WeightDetails>()
    val selectedWeightDetail
        get() = _selectedWeightDetail

    private var _weightDetails = MutableLiveData<List<WeightDetails>>()
    val weightDetails
        get() = _weightDetails




    init{
        _dateSelectedUTC.value= MaterialDatePicker.todayInUtcMilliseconds()
        _dateSelected.value="Today"
    }

    fun setDateSelectedUTC(date: Long){
        _dateSelectedUTC.value=date
    }

    fun setDateSelected(date:String){
        _dateSelected.value=date
    }

    fun setWeightSelected(weightDetails: WeightDetails){
        _selectedWeightDetail.value=weightDetails
    }

    fun insertWeightDetails(weight:Double)=viewModelScope.launch {
        val weightDetails= WeightDetails(
            0,
            sessionManager.getUserId(),
            weight,
            _dateSelectedUTC.value!!.toLong()
        )
        foodRepository.insertWeightDetails(weightDetails)
    }

    fun getWeightDetails()=foodRepository.getWeightDetails(sessionManager.getUserId())

    fun updateWeightDetails(weightDetails: WeightDetails)=viewModelScope.launch { foodRepository.updateWeightDetails(weightDetails) }

    fun setUpdatedWeight(newWeight: Double) {
        _selectedWeightDetail.value!!.weight=newWeight
        updateWeightDetails(_selectedWeightDetail.value!!)
    }

    fun deleteWeightDetails()=viewModelScope.launch {
        foodRepository.deleteWeightDetails(_selectedWeightDetail.value!!)
    }


}