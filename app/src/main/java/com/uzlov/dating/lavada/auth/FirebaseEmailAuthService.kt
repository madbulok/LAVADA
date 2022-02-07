package com.uzlov.dating.lavada.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class FirebaseEmailAuthService @Inject constructor(private val auth: FirebaseAuth) : IAuth<User, Activity> {

    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /**
         * Этот обратный вызов будет вызываться в двух ситуациях:
         * 1 - Мгновенная проверка. В некоторых случаях номер телефона может быть мгновенно
         * проверено без необходимости отправлять или вводить проверочный код.
         * 2 - Автозагрузка. На некоторых устройствах сервисы Google Play могут автоматически
         * обнаруживаем входящее проверочное SMS и выполняем проверку без
         * действие пользователя.
         * */
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        /**
         * Этот обратный вызов вызывается при выполнении недопустимого запроса на проверку,
         * например, если формат номера телефона недействителен.
         **/
        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Неверный запрос

            } else if (e is FirebaseTooManyRequestsException) {
                //Превышена квота SMS для проекта

            }
        }

        /***
         * Проверочный код по SMS был отправлен на указанный номер телефона,
         * теперь нужно попросить пользователя ввести код, а затем создать учетные данные
         * путем объединения кода с идентификатором подтверждения.
         */
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // Сохраните идентификатор подтверждения и токен повторной отправки, чтобы мы могли использовать их позже
            storedVerificationId = verificationId
            resendToken = token
        }

    }

    fun checkCredential(code: String) {
        val credential = storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // обработать callback
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    override fun startAuth(user: User, host: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(user.phone)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(host)                 // Activity (for callback binding) TODO(нужно как то избавиться от активности здесь)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun logout() {
        auth.signOut()
    }

}


