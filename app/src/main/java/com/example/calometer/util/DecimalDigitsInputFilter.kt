package com.example.calometer.util

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeDecimal: Int, digitsAfterDecimal: Int) : InputFilter {

    private val mPattern: Pattern = Pattern.compile("[0-9]{0,$digitsBeforeDecimal}+((\\.[0-9]{0,$digitsAfterDecimal})?)||(\\.)?")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher: Matcher = mPattern.matcher(
            dest?.subSequence(0, dstart).toString() + source?.subSequence(
                start,
                end
            ).toString() + dest?.subSequence(dend, dest.length).toString()
        )
        return if (!matcher.matches())
            ""
        else
            null
    }
}