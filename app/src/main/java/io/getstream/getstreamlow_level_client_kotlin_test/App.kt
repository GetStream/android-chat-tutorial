package io.getstream.getstreamlow_level_client_kotlin_test

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.ChatConfig
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.logger.ChatLoggerHandler
import io.getstream.chat.android.client.logger.ChatLoggerImpl
import io.getstream.chat.android.client.logger.ChatLoggerLevel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.notifications.ChatNotificationConfig
import io.getstream.chat.android.client.notifications.DeviceRegisteredListener
import io.getstream.chat.android.client.notifications.NotificationMessageLoadListener
import io.getstream.chat.android.client.notifications.options.ChatNotificationOptions
import io.getstream.chat.android.client.notifications.options.NotificationIntentProvider

class App : Application() {

    private val TAG = this::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        val loggerHandler: ChatLoggerHandler = object : ChatLoggerHandler {
            override fun logT(throwable: Throwable) {
                // display throwable logs here
            }

            override fun logT(className: String, throwable: Throwable) {
                // display throwable logs here
            }

            override fun logI(className: String, message: String) {
                // display info logs here
            }

            override fun logD(className: String, message: String) {
                // display debug logs here
            }

            override fun logW(className: String, message: String) {
                // display warning logs here
            }

            override fun logE(className: String, message: String) {
                // display error logs here
            }
        }

        val logger = ChatLoggerImpl.Builder()
            .level(if (BuildConfig.DEBUG) ChatLoggerLevel.ALL else ChatLoggerLevel.NOTHING)
            .handler(loggerHandler)
            .build()

        //Prod base url: chat-us-east-1.stream-io-api.com
        val config = ChatConfig.Builder()
            .apiKey("qk4nn7rpcn75")
            .baseUrl("chat-us-east-staging.stream-io-api.com")
            .cdnUrl("chat-us-east-staging.stream-io-api.com")
            .baseTimeout(10000)
            .cdnTimeout(10000)
            .token("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiYmVuZGVyIn0.3KYJIoYvSPgTURznP8nWvsA2Yj2-vLqrm-ubqAeOlcQ")
            .build()

        val notificationConfig = ChatNotificationConfig.Builder()
            .options(provideNotificationOptions())
            .registerListener(provideDeviceRegisteredListener())
            .messageLoadListener(provideNotificationMessageLoadListener())
            .build()

        val clientBuilder = ChatClient.Builder()
            .config(config)
            .logger(logger)
            .notification(notificationConfig)

        ChatClient.init(clientBuilder)
    }

    private fun provideNotificationOptions() = ChatNotificationOptions().apply {
        setNotificationIntentProvider(
            object : NotificationIntentProvider {
                override fun getIntentForFirebaseMessage(
                    context: Context,
                    remoteMessage: RemoteMessage
                ): PendingIntent {
                    val payload = remoteMessage.data
                    val intent = Intent(context, MainActivity::class.java)

                    return PendingIntent.getActivity(
                        context, 999,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                override fun getIntentForWebSocketEvent(
                    context: Context,
                    event: ChatEvent
                ): PendingIntent {
                    val intent = Intent(context, MainActivity::class.java)
                    return PendingIntent.getActivity(
                        context, 999,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            }
        )
    }

    private fun provideDeviceRegisteredListener() = object : DeviceRegisteredListener {
        override fun onDeviceRegisteredSuccess() { // Device successfully registered on server
            Log.i(TAG, "Device registered successfully")
        }

        override fun onDeviceRegisteredError(error: ChatError) {
            Log.e(TAG, "onDeviceRegisteredError: ${error.message}")
        }
    }

    private fun provideNotificationMessageLoadListener() =
        object : NotificationMessageLoadListener {
            override fun onLoadMessageSuccess(message: Message) {
                Log.d(TAG, "On message loaded. Message:$message")
            }

            override fun onLoadMessageFail(messageId: String) {
                Log.d(TAG,"Message from notification load fails. MessageId:$messageId")
            }
        }
}