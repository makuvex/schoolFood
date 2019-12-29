package com.jungbae.schoolfood

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jungbae.schoolfood.network.FBSchoolData
import com.jungbae.schoolfood.network.SimpleSchoolData
import com.jungbae.schoolfood.network.preference.PreferenceManager
import kotlin.properties.Delegates

class  SchoolFoodApplication : Application() {

    companion object {
        var context: Context by Delegates.notNull()
            private set
        lateinit var preferences: PreferenceManager

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        preferences = PreferenceManager()
        //createNotificationChannel()
    }
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel("학교 급식", "학교 급식 알림", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "학교 급식 알림"
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 100, 200)
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun sendBroadcastWith(intent: Intent) {
        applicationContext.startActivity(intent)
    }
}
