package com.icerockdev.library.classifier.maping

/**
 * This will map classifier [I] result into DTO [O] model
 *
 * @see DigitMapper
 */
interface ClassifierMapper<in I : Any, out O : Any> {
    fun map(output: I, index: Int): O
}
