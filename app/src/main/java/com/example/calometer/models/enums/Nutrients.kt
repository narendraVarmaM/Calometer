package com.example.calometer.models.enums

enum class Nutrients(val headingValue: String, val units:String) {
    Calories("Calories", "kcal"),
    Fat("Fat","g"),
    Protein("Protein","g"),
    Carbohydrates("Carbohydrates","g"),
    Sugars("Sugars","g"),
    Fiber("Fiber","g"),
    Cholesterol("Cholesterol","mg"),
    SaturatedFats("Saturated Fat","g"),
    Calcium("Calcium","mg"),
    Iron("Iron","mg"),
    Potassium("Potassium","mg"),
    Magnesium("Magnesium", "mg"),
    Sodium("Sodium","mg"),
    MonoUnsaturatedFat("Monounsaturated Fat", "mg"),
    PolyUnsaturatedFat("Polyunsaturated Fat", "mg")
}