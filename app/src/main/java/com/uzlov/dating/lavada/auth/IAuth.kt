package com.uzlov.dating.lavada.auth

interface IAuth<T, A> {
    fun login(T: String, A: String)
    fun logout()
}