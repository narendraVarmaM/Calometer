package com.example.calometer.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "userTable")
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId:Long=0,
    var name:String,
    val mobileNo:Long,
    var password:String,
    var age:Int,
    var height:Int,
    var startWeight:Double,
    var goal:String,
    var goalWeight:Double,
    var gender:String,
    var activityLevel:String,
    var calorieGoal:Int
):Serializable