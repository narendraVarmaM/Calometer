package com.example.calometer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calometer.MyApplication
import com.example.calometer.repository.FoodRepository
import com.example.calometer.database.SessionManager
import com.example.calometer.models.entity.User
import com.example.calometer.models.entity.WeightDetails
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val application: MyApplication,
    private val foodRepository: FoodRepository
):ViewModel(){


    private val _navigateToHomeFragment = MutableLiveData<Boolean?>()
    val navigateToHomeFragment
        get() = _navigateToHomeFragment

    private val _navigateToLoginFragment = MutableLiveData<Boolean?>()
    val navigateToLoginFragment
        get() = _navigateToLoginFragment

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage
        get() = _showToastMessage

    fun validateUser(mobileNo: Long, password: String) = viewModelScope.launch {
        foodRepository.getUserWithMobileNo(mobileNo)?.let {
            if (it.password == password) {
                _showToastMessage.value = "Logged in!"
                _navigateToHomeFragment.value=true
                it.apply {
                    val sessionManager = SessionManager(application)
                    sessionManager.createLoginSession(
                        name,
                        mobileNo,
                        password,
                        userId,
                        height,
                        calorieGoal
                    )
                    Log.e("HomeViewModel", "${sessionManager.getUserDetailsFromSession()}")
                }
            } else {
                _showToastMessage.value = "Incorrect password!"
            }
        } ?: run {
            _showToastMessage.postValue("Mobile no is not registered!")
        }
    }

    fun insertUser(user: User, weight: Double)=viewModelScope.launch {
        val id = foodRepository.insertUser(user)
        insertWeightDetails(weight, id)
    }

    private fun insertWeightDetails(weight:Double, id:Long)=viewModelScope.launch {
        val weightDetails= WeightDetails(
            0,
            id,
            weight,
            MaterialDatePicker.todayInUtcMilliseconds()
        )
        foodRepository.insertWeightDetails(weightDetails)
        _navigateToLoginFragment.value=true
        _showToastMessage.value = "Log in to continue!"
    }


    fun doneShowingToastMessage() {
        _showToastMessage.value = null
    }

    fun doneNavigatingToHomeFragment(){
        _navigateToHomeFragment.value = null
    }

    fun doneNavigatingToLoginFragment(){
        _navigateToHomeFragment.value = null
    }


}