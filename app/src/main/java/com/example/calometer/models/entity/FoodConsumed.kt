package com.example.calometer.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "foodConsumed")
data class FoodConsumed(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val dayId:Long,
    val mealType:String,
    var quantity:Int,
    val foodId:Int,
    val userId:Long,
    var calories:Int,
    val foodName:String
    ):Serializable
