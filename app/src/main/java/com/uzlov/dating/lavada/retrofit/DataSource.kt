package com.uzlov.dating.lavada.retrofit

interface DataSource<T> {

    suspend fun getData(): T
}