package com.uzlov.dating.lavada.auth

interface IAuth<T, A> {
    fun startAuth(user: T, host: A)
    fun logout()
}