package com.example.core

/**
 * A sealed class representing the result of an operation.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents an error operation with an exception.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Represents a loading state.
     */
    object Loading : Result<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Returns true if this is an Error result.
     */
    fun isError(): Boolean = this is Error

}