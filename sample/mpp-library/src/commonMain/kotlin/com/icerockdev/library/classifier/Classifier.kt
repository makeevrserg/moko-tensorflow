package com.icerockdev.library.classifier

/**
 * Classifier interface which will [I] into [O] with tflite model
 */
interface Classifier<in I : Any, out O : Any> {
    /**
     * Classify function
     */
    fun classify(input: I): O

    interface Options {
        /**
         * Generally models are created like [Nx1x1x1]
         * where N is number of batches
         *
         * Batch's position is constant so we define [inputTensorIndex]
         */
        fun inputShape(batchSize: Int): IntArray

        /**
         * Index of batch in array
         */
        val inputTensorIndex: Int
    }
}
