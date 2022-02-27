package com.uzlov.dating.lavada.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.domain.models.User
import java.lang.IllegalStateException
import javax.inject.Inject


class FirebaseEmailAuthService @Inject constructor(val auth: FirebaseAuth) : IAuth<User, Activity> {

    private var mToken: String? = null
    private var user: User? = null

    //регистрация нового пользователя. Умеет обрабатывать ошибки регистрации (например, если такой email уже есть в системе
    fun registered(
        login: String,
        password: String
    ) : Task<AuthResult>{
        return auth.createUserWithEmailAndPassword(login, password)
    }

    // вспомогательный метод для входа через google, .requestIdToken - это из Web client (Auto-created for Google Sign-in)
    //без этой стринги вход через гугл не работает(
    // https://console.developers.google.com/apis/credentials?project=lavada-7777 - лежит тут
    //у меня автоматом собирается из
    fun getGSO(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }


    fun setToken(idToken: String) {
        mToken = idToken
    }

    override fun login(t: User, a: Activity) {
        if (mToken == null) throw IllegalStateException("Авторизация не увенчалась успехом =(")
        val credential = GoogleAuthProvider.getCredential(mToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(a) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithGoogle:success")
                } else {
                    Log.w(TAG, "createUserWithGoogle:failure", task.exception)
                }
            }
    }

    // непосредственный вход через google, создает аккаунт
    fun loginWithGoogleAccount() : Task<AuthResult> {
        if (mToken == null) throw IllegalStateException("Авторизация не увенчалась успехом =(")
        val credential = GoogleAuthProvider.getCredential(mToken, null)
        return auth.signInWithCredential(credential)
    }

    //вход с помощью email и password, не создает аккаунт, проверяет наличие, возвращает task.exception.localizedMessage
    fun loginWithEmailAndPassword(
        email: String,
        password: String
    ):  Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun logout() {
        auth.signOut()
    }

    //удаляем пользователя совсем (пока не связан с database, данные у бд не удаляет)
    fun delUser() {
        val user = auth.currentUser!!
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                }
            }
    }

    fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    companion object {
        const val TAG = "EmailPassword"
    }
}


