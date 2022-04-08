package com.uzlov.dating.lavada.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.domain.models.ChatMessage
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.viemodels.MessageChatViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewMessageService : Service(), LifecycleOwner {

    @Inject
    lateinit var authService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryModel: ViewModelFactory
    private lateinit var chatModel: MessageChatViewModel
    private lateinit var userViewModel: UsersViewModel

    interface NewMessageStateListener {
        fun newMessage(
            item: String,
            chatMessage: ChatMessage
        ) // прислали новое сообщение
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private val messageCallback: NewMessageStateListener =
        object : NewMessageStateListener {
            override fun newMessage(item: String, chatMessage: ChatMessage) {
                GlobalScope.launch {
                    userViewModel.getUserSuspend(item)?.let { user ->
                        notify(
                            User(), "Новое сообщение от " + user.name,
                            chatMessage.message
                        )
                    }
                }
            }
        }

    override fun onCreate() {
        lifecycleRegistry = LifecycleRegistry(this)
        applicationContext.appComponent.inject(this)
        super.onCreate()
        chatModel = factoryModel.create(MessageChatViewModel::class.java)
        userViewModel = factoryModel.create(UsersViewModel::class.java)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onBind(p0: Intent?): IBinder? {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        authService.getUserUid().let { uid ->
            /**тут нужно нормальное решение, пока хардкод**/
            chatModel.observeMessage("36b5b561-b690-4340-8ee0-11970aae1ed6", messageCallback)
        }

        return START_STICKY
    }

    private fun notify(user: User, title: String, content: String) {
        val notificationHelper =
            NotificationHelper(
                applicationContext,
                title, content
            )
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}