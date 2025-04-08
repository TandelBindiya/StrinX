package com.bindiya.strinx.data.datasource

sealed class Result<T>(val data: T? = null, val errorMessage: String) {
    class Success<T>(data: T) : Result<T>(data, "")
    class Error<T>(errorMessage: String) : Result<T>(null, errorMessage)
}