package com.uzlov.dating.lavada.auth

interface IAuth<T, A> {
    fun login(t: T, a: A)
    fun logout()
}