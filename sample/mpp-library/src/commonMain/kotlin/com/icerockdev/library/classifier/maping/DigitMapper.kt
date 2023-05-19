package com.icerockdev.library.classifier.maping

import com.icerockdev.library.classifier.DigitClassifier
import com.icerockdev.library.classifier.DigitClassifier.Output
import com.icerockdev.library.classifier.DigitClassifier.Digit

object DigitMapper : ClassifierMapper<Output, DigitClassifier.MappedResult> {

    override fun map(output: Output, index: Int): DigitClassifier.MappedResult {
        val digits = output.rawArray[index]
        val maxIndex = digits.indices.maxByOrNull { digits[it] } ?: -1
        if (maxIndex == -1) return DigitClassifier.MappedResult()
        val digit = Digit.values()[maxIndex]
        return DigitClassifier.MappedResult(
            digit = digit,
            percent = digits[maxIndex],
            index = maxIndex
        )
    }

}
