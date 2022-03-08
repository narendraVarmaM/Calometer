package com.example.calometer.database

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    companion object{
        const val IS_LOGIN = "isLoggedIn"
        const val KEY_NAME = "name"
        const val KEY_MOBILE_NUMBER = "mobileNumber"
        const val KEY_PASSWORD = "password"
        const val KEY_USER_ID = "userId"
        const val KEY_USER_HEIGHT = "height"
        const val KEY_USER_CALORIE = "calorieGoal"
    }
    private val userSession: SharedPreferences = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = userSession.edit()

    fun createLoginSession(name: String, mobileNo: Long, password: String, userId:Long, height:Int, calorieGoal:Int){
        editor.apply {
            putBoolean(IS_LOGIN, true)
            putString(KEY_NAME, name)
            putLong(KEY_MOBILE_NUMBER, mobileNo)
            putString(KEY_PASSWORD, password)
            putLong(KEY_USER_ID, userId)
            putInt(KEY_USER_HEIGHT, height)
            putInt(KEY_USER_CALORIE, calorieGoal)
            commit()
        }
    }

    fun getUserId() = userSession.getLong(KEY_USER_ID, 1)

    fun getHeight() = userSession.getInt(KEY_USER_HEIGHT, 1)

    fun getCalorieGoal() = userSession.getInt(KEY_USER_CALORIE, 1)

    fun getUserDetailsFromSession(): Map<String, *> = userSession.all
    fun checkLogin() = userSession.getBoolean(IS_LOGIN, false)
    fun logoutUserFromSession(){
        editor.clear()
        editor.commit()
    }

    fun setCalorieGoal(calorieGoal: Int) {
        editor.putInt(KEY_USER_CALORIE, calorieGoal)
        editor.apply()
    }
}