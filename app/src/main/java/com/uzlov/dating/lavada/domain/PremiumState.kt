package com.uzlov.dating.lavada.domain

/**
 * Объект для обмена состоянием подписки (покупки)
* */
sealed class PremiumState<T> {

    /**
     * Успешно. Хранит в себе транзакцию
     */
    class Success<T>(val result: T) : PremiumState<T>()

    /**
     * Ошибка. Содержит сообщение об ошибке
    * */
    class Error<T>(val message: String) : PremiumState<T>()
}