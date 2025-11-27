package com.roadi.budgesfram.kgoed.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.roadi.budgesfram.R
import com.roadi.budgesfram.RoadBudgetFarmActivity
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication

private const val ROAD_BUDGET_FARM_CHANNEL_ID = "road_budget_farm_notifications"
private const val ROAD_BUDGET_FARM_CHANNEL_NAME = "RoadBudgetFarm Notifications"
private const val ROAD_BUDGET_FARM_NOT_TAG = "RoadBudgetFarm"

class RoadBudgetFarmPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                roadBudgetFarmShowNotification(it.title ?: ROAD_BUDGET_FARM_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                roadBudgetFarmShowNotification(it.title ?: ROAD_BUDGET_FARM_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            roadBudgetFarmHandleDataPayload(remoteMessage.data)
        }
    }

    private fun roadBudgetFarmShowNotification(title: String, message: String, data: String?) {
        val roadBudgetFarmNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ROAD_BUDGET_FARM_CHANNEL_ID,
                ROAD_BUDGET_FARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            roadBudgetFarmNotificationManager.createNotificationChannel(channel)
        }

        val roadBudgetFarmIntent = Intent(this, RoadBudgetFarmActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val roadBudgetFarmPendingIntent = PendingIntent.getActivity(
            this,
            0,
            roadBudgetFarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val roadBudgetFarmNotification = NotificationCompat.Builder(this, ROAD_BUDGET_FARM_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.road_budget_farm_noti)
            .setAutoCancel(true)
            .setContentIntent(roadBudgetFarmPendingIntent)
            .build()

        roadBudgetFarmNotificationManager.notify(System.currentTimeMillis().toInt(), roadBudgetFarmNotification)
    }

    private fun roadBudgetFarmHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}