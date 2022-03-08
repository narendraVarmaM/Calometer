package com.example.calometer.models.enums

enum class Goal(val displayValue:String, val factor:Int) {
    Loss("Weight Loss",-500),
    Maintain("Maintain my current weight",0),
    Gain("Weight gain",500)
}