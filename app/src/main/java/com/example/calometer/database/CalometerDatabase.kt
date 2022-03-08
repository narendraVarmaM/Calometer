package com.example.calometer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.models.entity.FoodData
import com.example.calometer.models.entity.User
import com.example.calometer.models.entity.WeightDetails

@Database(entities = [FoodData::class,
                     FoodConsumed::class,
                     User::class,
                     WeightDetails::class],version = 1, exportSchema = false)
abstract class CalometerDatabase:RoomDatabase() {
    abstract val foodDataDao: FoodDataDao
    abstract val userDataDao: UserDataDao
}