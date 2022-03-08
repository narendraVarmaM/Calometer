package com.example.calometer.models.enums

enum class ActivityLevel(val displayValue:String, val factor:Double) {
    Sedentary("Sedentary",1.2),
    Low("Low Active",1.375),
    Medium("Active",1.55),
    High("Very Active",1.725)
}