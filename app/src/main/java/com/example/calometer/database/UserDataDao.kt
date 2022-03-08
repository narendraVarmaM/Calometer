package com.example.calometer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calometer.models.entity.User
import com.example.calometer.models.entity.WeightDetails

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User):Long

    @Insert
    suspend fun insertWeightDetails(weightDetails: WeightDetails)

    @Query("SELECT * FROM weightDetails WHERE userId = :userId")
    fun getWeightDetails(userId:Long):LiveData<List<WeightDetails>>

    @Update
    suspend fun updateWeightDetails(weightDetails: WeightDetails)

    @Delete
    suspend fun deleteWeightDetails(weightDetails: WeightDetails)

    @Transaction
    @Query("Select * from userTable where mobileNo = :mobileNo limit 1")
    suspend fun getUserWithMobileNo(mobileNo: Long): User?

    @Query("Select * from userTable where userId = :userId")
    fun getUser(userId: Long): LiveData<User>

    @Update
    suspend fun updateUser(user: User)

}