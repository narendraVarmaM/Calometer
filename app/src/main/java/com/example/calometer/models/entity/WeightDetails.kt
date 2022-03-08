package com.example.calometer.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weightDetails")
data class WeightDetails(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val userId:Long,
    var weight:Double,
    val date:Long
)
