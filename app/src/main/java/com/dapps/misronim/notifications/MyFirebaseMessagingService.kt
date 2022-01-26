package com.dapps.misronim.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dapps.misronim.ui.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.os.Bundle
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

import android.widget.RemoteViews
import com.dapps.misronim.R
import com.dapps.misronim.ui.MainActivity





class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("MyFireBaseNessagingService1", "Message ${remoteMessage.data}")

         val intent = remoteMessage.toIntent().extras
             val title = intent?.get("title")
             val message = intent?.get("message")
             val profileImage = intent?.get("profileImage")

        Log.e("MyFireBaseNessagingService2", "Message received $title $message $profileImage ")

        sendNotification(remoteMessage)

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("NEW_TOKEN", p0)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        Log.e("MyFireBaseNessagingService3", "sendNotification")

        val userName = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val profileImage = remoteMessage.data["profileImage"]
        val senderID = remoteMessage.data["senderUID"]

        val image = getBitmapfromUrl(profileImage)

        val smallView = RemoteViews(packageName, R.layout.notification_layout)
        smallView.setTextViewText(R.id.notificationTitle, "$userName:")
        smallView.setTextViewText(R.id.notificationBody, message)
        smallView.setImageViewBitmap(R.id.notificationUserImage, image)



        val intent = Intent(this,ChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString("senderUID", senderID)
        intent.putExtras(bundle)

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
        stackBuilder.addNextIntent(intent)
        val pendingIntent: PendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)


        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this,getString(com.dapps.misronim.R.string.default_notification_channel_id))
            .setSmallIcon(com.dapps.misronim.R.drawable.misronim_logo)
            .setLargeIcon(image)
            .setContentTitle(userName)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)
            .setCustomContentView(smallView)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, builder.build())

    }


    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }


}