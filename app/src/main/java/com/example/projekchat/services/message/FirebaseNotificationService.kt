package com.example.projekchat.services.message

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.projekchat.R
import com.example.projekchat.ui.home.ui.chat.ChatFragment
import com.example.projekchat.utils.Constant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseNotificationService:FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
//            val hisImage = map["hisImage"]
            val chatId = map["chatId"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                createOreoNotification(title!!, message!!, hisId!!, chatId!!)
            }
            else createNormalNotification(title!!, message!!, hisId!!, chatId!!)

        }
    }

    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String,
        chatId: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, Constant.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.avatar_man)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.color_primary, null))
            .setSound(uri)

        val intent = Intent(this, ChatFragment::class.java) // BISA JADI HOME ACTIVITY CLASS JAVA

        intent.putExtra("hisId", hisId)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String,
        chatId: String
    ) {

        val channel = NotificationChannel(
            Constant.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatFragment::class.java)

        intent.putExtra("hisId", hisId)
//        intent.putExtra("hisImage", hisImage)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, Constant.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo_yourschat)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.color_primary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }
}