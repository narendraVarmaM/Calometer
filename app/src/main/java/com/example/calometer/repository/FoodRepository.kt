package com.example.calometer.repository

import com.example.calometer.database.*
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.models.entity.User
import com.example.calometer.models.entity.WeightDetails


class FoodRepository constructor(
    private val foodDataDao: FoodDataDao,
    private val userDataDao: UserDataDao
) {
    fun search(searchString: String) = foodDataDao.search(searchString)


    fun getFoodConsumed(date:Long, userId: Long)= foodDataDao.getFoodConsumed(date, userId)

    fun getFoodDetailsById(id:Int)=foodDataDao.getFoodDataById(id)

    suspend fun insertFoodConsumed(foodConsumed: FoodConsumed)=foodDataDao.insertFoodConsumed(foodConsumed)

    suspend fun updateFoodConsumed(foodConsumed: FoodConsumed)=foodDataDao.updateFoodConsumed(foodConsumed)

    suspend fun deleteFoodConsumed(foodConsumed: FoodConsumed)=foodDataDao.deleteFoodConsumed(foodConsumed)

    suspend fun insertWeightDetails(weightDetails: WeightDetails)=userDataDao.insertWeightDetails(weightDetails)

    fun getWeightDetails(userId:Long)=userDataDao.getWeightDetails(userId)

    suspend fun updateWeightDetails(weightDetails: WeightDetails)=userDataDao.updateWeightDetails(weightDetails)

    suspend fun deleteWeightDetails(weightDetails: WeightDetails)=userDataDao.deleteWeightDetails(weightDetails)

    suspend fun getUserWithMobileNo(mobileNo: Long) = userDataDao.getUserWithMobileNo(mobileNo)

    suspend fun insertUser(user: User)= userDataDao.insertUser(user)

    suspend fun updateUser(user: User)= userDataDao.updateUser(user)

    fun getUser(userId: Long)=userDataDao.getUser(userId)

    suspend fun getFoodDetailsById(listOfFoodId:List<Int>)=foodDataDao.getFoodDataById(listOfFoodId)


}