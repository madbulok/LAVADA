package com.uzlov.dating.lavada.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.activities.SplashActivity

class NotificationHelper(
private val context: Context,
private val title: String,
private val contentText: String,
) {


    // Constants for the notification actions buttons.
    private val ACTION_UPDATE_NOTIFICATION =
        "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION"

    // Notification channel ID.
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"

    // Notification ID.
    companion object {
        private var NOTIFICATION_ID = 0
    }

    private val mNotifyManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    fun createNotificationChannel() {
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "main_channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "description"

            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {

        // Set up the pending intent that is delivered when the notification
        // is clicked.
        val notificationIntent = Intent(context, SplashActivity::class.java)

        val notificationPendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_ID, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification with all of the parameters.
        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.one)
            .setAutoCancel(true)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }


    fun sendNotification() {

        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT
        )

        // Build the notification with all of the parameters using helper
        // method.
        val notifyBuilder = getNotificationBuilder()


        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        // Enable the update and cancel buttons but disables the "Notify
        // Me!" button.
        //setNotificationButtonState(false, true, true)

        NOTIFICATION_ID++
    }

    inner class NotificationReceiver : BroadcastReceiver() {

        /**
         * Receives the incoming broadcasts and responds accordingly.
         *
         * @param context Context of the app when the broadcast is received.
         * @param intent The broadcast intent containing the action.
         */
        override fun onReceive(context: Context, intent: Intent) {
            // Update the notification.
            //updateNotification()
        }
    }
}