package com.uzlov.dating.lavada.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject


class MatchesService : Service(), LifecycleOwner {

    @Inject
    lateinit var authService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryModel: ViewModelFactory
    private lateinit var usersModel: UsersViewModel

    interface MatchesStateListener {
        fun match(item: User) // прислали лайк
        fun mutualMatch(item: User) // прислали взаимный лайк
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private val matchesCallback: MatchesStateListener =
        object : MatchesStateListener {
            override fun match(item: User) {
                notify(item, "У вас новый лайк",
                    item.name + " вас лайкнул")
            }

            override fun mutualMatch(item: User) {
                notify(item, "У вас новый взаимный лайк",
                    item.name + " вас лайкнул")
            }
        }

    override fun onCreate() {
        lifecycleRegistry = LifecycleRegistry(this)
        applicationContext.appComponent.inject(this)
        super.onCreate()
        usersModel = factoryModel.create(UsersViewModel::class.java)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onBind(p0: Intent?): IBinder? {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        authService.getUserUid().let { uid ->
            if (uid != null) {

            }
        }
        return START_STICKY
    }

    private fun notify(user: User, title: String, content: String) {
        val notificationHelper =
            NotificationHelper(
                applicationContext,
                title, content)
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}