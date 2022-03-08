package com.example.calometer.util

import android.util.Log
import com.example.calometer.models.enums.Nutrients
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class Util {
    companion object {
        fun convertUTCToDate(date: Long): String {
            return SimpleDateFormat("dd MMM yyyy").format(date)
        }

        fun convertUTCToDateYY(date: Long): String {
            return SimpleDateFormat("dd MMM yy").format(date)
        }


        fun getNutritionalValuesWithUnits(nutrients:List<Double?>):List<String>{
            val result = mutableListOf<String>()
            val units:List<String> = Nutrients.values().map { it.units }
            Log.i("in util","double size is {${nutrients.size}}")
            Log.i("in util","double size is {${units.size}}")

            units.forEachIndexed { index, s ->
                nutrients[index]?.let{
                        result.add(index,roundOffDecimal(it).toString() + " " +s)
                }?: run { result.add(index, "-") }
            }
            return result
        }
        fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }
        fun formatDecimal(number: Double): String {
            return String.format("%.1f", number)
        }
    }
}