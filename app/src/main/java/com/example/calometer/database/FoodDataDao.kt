package com.example.calometer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.models.entity.FoodData

@Dao
interface FoodDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodConsumed(foodConsumed: FoodConsumed)

    @Query("SELECT * FROM foodData WHERE foodName LIKE :searchString")
    fun search(searchString:String): LiveData<List<FoodData>>

    @Query("SELECT * FROM foodData WHERE id = :id")
    fun getFoodDataById(id:Int): LiveData<FoodData>

    @Query("SELECT * FROM foodConsumed WHERE dayId = :date AND userId = :userId")
    fun getFoodConsumed(date:Long, userId:Long):LiveData<List<FoodConsumed>>

    @Update
    suspend fun updateFoodConsumed(foodConsumed: FoodConsumed)

    @Delete
    suspend fun deleteFoodConsumed(foodConsumed: FoodConsumed)

    @Query("SELECT * FROM foodData WHERE id in (:listOfFoodId)")
    suspend fun getFoodDataById(listOfFoodId:List<Int>): List<FoodData>
}