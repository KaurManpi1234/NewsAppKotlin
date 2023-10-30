package com.example.newsappkotlin

sealed class Resource<T>(val data: T?=null,val error:String?=null){
    class Success<T>(data:T):Resource<T>(data)
    class Loading<T>: Resource<T>()

    class Error<T>(error: String, data: T? = null): Resource<T>(data, error)

}