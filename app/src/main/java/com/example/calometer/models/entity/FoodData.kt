package com.example.calometer.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "foodData")
data class FoodData(
    @PrimaryKey val id:Int,
    val foodId:Int,
    val foodName:String,
    val foodGroup:String?,
    val calories:Double,
    val fat:Double,
    val protein:Double,
    val carbohydrate:Double,
    val sugars:Double?,
    val fiber:Double?,
    val cholesterol:Double?,
    val saturatedFats:Double?,
    val calcium:Double?,
    val iron:Double?,
    val potassium:Double?,
    val magnesium:Double?,
    val sodium:Double?,
    val monoUnsaturatedFat:Double?,
    val polyUnsaturatedFat:Double?,
    val servingWeight:Double?,
    val servingDescription:String?
): Serializable

