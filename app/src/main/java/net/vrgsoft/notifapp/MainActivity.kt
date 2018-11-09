package net.vrgsoft.notifapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import net.vrgsoft.notifapp.R.id.tvBuildVariant
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 1
        tvBuildVariant.text = BuildConfig.FLAVOR + BuildConfig.BUILD_TYPE
        // 2
        createNotificationChannel()
        if (!isNotificationVisible()) {
            makeNotification()
        }
    }

    private fun makeNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(this, NOTIF_ID, notificationIntent, 0)

        var builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_NAME)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Title")
            .setContentText("Text")
            .setTicker("Ticker Text")
            .setWhen(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
            .setTimeoutAfter(TimeUnit.MINUTES.toMillis(5))
            .setUsesChronometer(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setContentIntent(intent)
            .setOnlyAlertOnce(true)
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIF_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIF_CHANNEL_NAME
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(name, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isNotificationVisible(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var activeNotifications = notificationManager.activeNotifications
            for (item in activeNotifications) {
                if (item.id == NOTIF_ID && item.packageName == BuildConfig.APPLICATION_ID) {
                    return true
                }
            }
        } else {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val test = PendingIntent.getActivity(this, NOTIF_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE)
            if (test != null) {
                return true
            }
        }
        return false
    }

    companion object {
        const val NOTIF_ID = 69
        const val NOTIF_CHANNEL_NAME = "Test channel"
    }
}
