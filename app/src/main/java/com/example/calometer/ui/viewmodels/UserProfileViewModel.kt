package com.example.calometer.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calometer.MyApplication
import com.example.calometer.repository.FoodRepository
import com.example.calometer.database.SessionManager
import com.example.calometer.models.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val application: MyApplication,
    private val foodRepository: FoodRepository
):ViewModel() {
    private val sessionManager=SessionManager(application)

    fun logoutCurrentUser()= sessionManager.logoutUserFromSession()

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage
        get() = _showToastMessage

    private var _currentUser = MutableLiveData<User>()
    val currentUser
        get() = _currentUser

    private val _navigateToHomeFragment = MutableLiveData<Boolean?>()
    val navigateToHomeFragment
        get() = _navigateToHomeFragment

    private val _navigateToProfileFragment = MutableLiveData<Boolean?>()
    val navigateToProfileFragment
        get() = _navigateToProfileFragment

    private val _navigateToWeightFragment = MutableLiveData<Boolean?>()
    val navigateToWeightFragment
        get() = _navigateToWeightFragment

    init {
        viewModelScope.launch {
            _currentUser.value=getUserDetails().value
        }
    }


    fun getUserDetails()=foodRepository.getUser(sessionManager.getUserId())

    fun updateUser(user: User)=viewModelScope.launch {
        sessionManager.setCalorieGoal(user.calorieGoal)
        foodRepository.updateUser(user)
        _navigateToHomeFragment.value = true
    }

    fun doneShowingToastMessage() {
        _showToastMessage.value = null
    }

    fun doneNavigatingToProfileFragment(){
        _navigateToProfileFragment.value = null
    }

    fun doneNavigatingToHomeFragment(){
        _navigateToHomeFragment.value = null
    }

    fun setCurrentUser(user: User) {
        _currentUser.value=user
    }

    fun updateUserGoalWeight(currentUser: User) =viewModelScope.launch {
        foodRepository.updateUser(currentUser)
        _navigateToWeightFragment.value=true
    }
    fun doneNavigatingToWeightFragment(){
        _navigateToWeightFragment.value = null
    }

    fun updateUserPassword(user: User)=viewModelScope.launch {
        foodRepository.updateUser(user)
        _navigateToProfileFragment.value = true
    }

}