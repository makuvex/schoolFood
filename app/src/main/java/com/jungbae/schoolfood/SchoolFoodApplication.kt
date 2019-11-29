package com.jungbae.schoolfood

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import kotlin.properties.Delegates

class  SchoolFoodApplication : Application() {

    companion object {
        var context: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel("학교 급식", "학교 급식 알림", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "학교 급식 알림"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun sendBroadcastWith(intent: Intent) {
        applicationContext.startActivity(intent)
    }
}
