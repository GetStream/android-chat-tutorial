package com.example.chattutorial

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.*
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.events.ConnectedEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.client.utils.FilterObject
import io.getstream.chat.android.client.utils.ProgressCallback
import kotlinx.android.synthetic.main.activity_tests.*
import java.io.File


/**
 * https://getstream.io/chat/docs/?language=kotlin
 */
class DocsActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    lateinit var client: ChatClient
    private lateinit var channel: Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests)

        initClient()

        client.events().subscribe { event ->
            if (event is ConnectedEvent) {
                testMainContainer.visibility = View.VISIBLE
            }
            Log.d(TAG, "OnEventReceive: $event")
        }

        initButtons()
    }

    private fun initClient() {

        /**
         * Typically done in your Application class
         */

        val apiKey = "qk4nn7rpcn75"
        val token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiYmVuZGVyIn0.3KYJIoYvSPgTURznP8nWvsA2Yj2-vLqrm-ubqAeOlcQ"
        val context = getApplicationContext()
        val client = ChatClient.Builder(apiKey, context).build()

        /**
         * Set the user to establish the websocket connection
         * Usually done when you open the chat interface
         */


        /**
         * extraData allows you to add any custom fields you want to store about your user
         * the UI components will pick up name and image by default
         */
        val user = User("bender")
        user.extraData["image"] = "https://bit.ly/321RmWb"
        user.extraData["name"] = "Bender"

        client.setUser(user, token, object : InitConnectionListener() {

            override fun onSuccess(data: ConnectionData) {
                val user = data.user
                val connectionId = data.connectionId
            }

            override fun onError(error: ChatError) {
                error.printStackTrace()
            }
        })

        this.client = client
    }

    private fun queryChannels() {

        /**
         * Note how the withWatch() argument ensures that we are watching the channel for any changes/new messages
         */

        val channelType = "messaging"
        val channelId = "uniq-channel-id"

        val channelController = client.channel(channelType, channelId)
        val request = ChannelQueryRequest().withMessages(20).withWatch()

        channelController.query(request).enqueue {
            if (it.isSuccess) {
                val channel = it.data()
            } else {
                it.error().printStackTrace()
            }
        }
    }

    private fun sendMessage() {

        /**
         * Prepare the message
         */
        val message = Message()
        message.text = "Hello world"

        val channelType = "messaging"
        val channelId = "uniq-channel-id"

        val channel = client.channel(channelType, channelId)

        /**
         * Send the message to the channel
         */
        channel.sendMessage(message).enqueue {
            if (it.isSuccess) {
                val message = it.data()
            } else {
                it.error().printStackTrace()
            }
        }
    }

    private fun events() {
        val subscription = client.events().subscribe { event ->
            if (event is NewMessageEvent) {
                val message = event.message
            }
        }
        subscription.unsubscribe()
    }


    private fun initButtons() {
        channelQueryBtn?.setOnClickListener {
            queryChannels()
        }
        channelWatchBtn?.setOnClickListener {
            watchChannel()
        }
        channelStopWatchingBtn?.setOnClickListener {
            stopWatching()
        }
        channelSendMessageBtn?.setOnClickListener {
            sendMessage()
        }
        channelShowBtn?.setOnClickListener {
            showChannel()
        }
        channelHideBtn?.setOnClickListener {
            hideChannel()
        }
        channelMessageMarkReadBtn?.setOnClickListener {
            markReadMessage()
        }
        channelAcceptInviteBtn?.setOnClickListener {
            acceptInvite()
        }
        channelRejectInviteBtn?.setOnClickListener {
            rejectInvite()
        }
        channelUpdateMsgBtn?.setOnClickListener {
            updateChannel()
        }
        getUsersBtn?.setOnClickListener {
            getUsers()
        }
        userAddMemberBtn?.setOnClickListener {
            addMembers()
        }
        userRemoveMemberBtn?.setOnClickListener {
            removeMembers()
        }
        userMuteUserBtn?.setOnClickListener {
            muteUser()
        }
        userUnMuteUserBtn?.setOnClickListener {
            unMuteUser()
        }
        userBanBtn?.setOnClickListener {
            banUser()
        }
        userUnBanBtn?.setOnClickListener {
            unBanUser()
        }

        messageSearchBtn?.setOnClickListener {
            searchMessages()
        }
        messageGetBtn?.setOnClickListener {
            getMessage()
        }
        messageUpdateBtn?.setOnClickListener {
            updateMessage()
        }
        messageSendActionBtn?.setOnClickListener {
            sendAction()
        }
        messageGetRepliesBtn?.setOnClickListener {
            getReplies()
        }
        messageGetReactionsBtn?.setOnClickListener {
            getReactions()
        }
        messageDeleteReactionsBtn?.setOnClickListener {
            deleteReaction()
        }
        messageDeleteBtn?.setOnClickListener {
            deleteMessage()
        }
        imageSendBtn?.setOnClickListener {
            sendFile(File("/sdcard/Download/sample.pdf"))
        }
        fileSendBtn?.setOnClickListener {
            sendFile(File("/sdcard/Download/sample_img.png"))
        }
        fileDeleteBtn?.setOnClickListener {
            deleteFile()
        }
        imageDeleteBtn?.setOnClickListener {
            deleteImage()
        }
    }


    private fun watchChannel() {

        val chController = client.channel("", "")

        Thread {
            val watchResult = chController.watch().execute()
            Log.d(TAG, "Watch: $watchResult")
        }.start()
    }

    private fun stopWatching() {
        client.stopWatching(
            channelType = channel.type,
            channelId = channel.id
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun showChannel() {
        client.showChannel(
            channelType = channel.type,
            channelId = channel.id
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun hideChannel() {
        client.hideChannel(
            channelType = channel.type,
            channelId = channel.id
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun markReadMessage() {
        client.markMessageRead(
            channelType = channel.type,
            channelId = channel.id,
            messageId = "message-id"
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun rejectInvite() {
        client.rejectInvite(
            channelType = channel.type,
            channelId = channel.id
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun acceptInvite() {
        client.acceptInvite(
            channelType = channel.type,
            channelId = channel.id,
            message = "hello-accept"
        ).enqueue {
            // Check result and show message
        }
    }

    private fun updateChannel() {
        val message = Message()
        message.text = "Hello"

        client.updateChannel(
            channelType = channel.type,
            channelId = channel.id,
            updateMessage = message
        ).enqueue {
            // Check result and show message
        }
    }

    private fun markAllRead() {
        client.markAllRead().enqueue {
            // Check result and show message
        }
    }

    private fun removeChannel() {
        client.deleteChannel(
            channelType = channel.type,
            channelId = channel.id
        ).enqueue {
            // Check result and show message
        }
    }

    private fun getUsers() {
        val filter = FilterObject("type", "messaging")

        /*val filter = FilterObject("banned", false)*/
        /*val filter = FilterObject("name", "bender")*/
        /*val filter = FilterObject("username", "bender")*/
        /*val filter = FilterObject("id", "bender")*/

        val sort = QuerySort().asc("last_active")
        val usersQuery = QueryUsersRequest(filter, 0, 10, sort)

        client.getUsers(usersQuery).enqueue { result ->
            // Check result and show message
        }
    }

    private fun addMembers() {
        client.addMembers(
            channelType = channel.type,
            channelId = channel.id,
            members = listOf("bender")
        ).enqueue { result ->
            //echoResult(result, "Member added successful")
        }
    }

    private fun removeMembers() {
        client.removeMembers(
            channelType = channel.type,
            channelId = channel.id,
            members = listOf("bender")
        ).enqueue { result ->
            //echoResult(result, "Member removed successful")
        }
    }

    private fun muteUser() {
        client.muteUser(
            targetId = "bender"
        ).enqueue { result ->
            //echoResult(result, "Member muted successful")
        }
    }

    private fun unMuteUser() {
        client.unMuteUser(
            targetId = "bender"
        ).enqueue { result ->
            //echoResult(result, "Member unmuted successful")
        }
    }

    private fun flag() {
        client.flag(
            targetId = "bender"
        ).enqueue { result ->
            //echoResult(result, "Flag successful")
        }
    }

    private fun banUser() {
        client.banUser(
            targetId = "bender",
            channelType = channel.type,
            channelId = channel.id,
            timeout = 10,
            reason = "reason"
        ).enqueue { result ->
            //echoResult(result, "User baned successful")
        }
    }

    private fun unBanUser() {
        client.unBanUser(
            targetId = "bender",
            channelType = channel.type,
            channelId = channel.id
        ).enqueue { result ->
            //echoResult(result, "User unbaned successful")
        }
    }

    private fun searchMessages() {
        val searchRequest = SearchMessagesRequest(
            query = "Hi",
            offset = 0,
            limit = 1,
            filter = FilterObject("type", "messaging")
        )
        client.searchMessages(searchRequest).enqueue { messageListResult ->
            // Check result and show message
        }
    }

    private fun getMessage() {
        client.getMessage(
            messageId = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8"
        )
            .enqueue { messageResult ->
                // Check result and show message
            }
    }

    private fun updateMessage() {
        val msg = Message().apply {
            id = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8"
            text = "Update text msg"
        }
        client.updateMessage(msg).enqueue { updatedMsg ->
            // Check result and show message
        }
    }

    private fun sendAction() {
        //Types "like","love","haha","wow","sad","angry"
        val action = SendActionRequest(
            channelId = channel.id,
            messageId = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8",
            type = "like",
            form_data = emptyMap()
        )
        client.sendAction(action).enqueue { result ->
            // Check result and show message
        }
    }

    private fun getReplies() {
        client.getReplies(
            messageId = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8",
            limit = 10
        ).enqueue { messageListResult ->
            // Check result and show message
        }
    }

    private fun getReactions() {
        client.getReactions(
            messageId = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8",
            limit = 10,
            offset = 0
        ).enqueue { reatcionsListResult ->
            // Check result and show message
        }
    }

    private fun deleteReaction() {
        //Types "like","love","haha","wow","sad","angry"

        client.deleteReaction(
            messageId = "YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8",
            reactionType = "like"
        ).enqueue { messageResult ->
            // Check result and show message
        }
    }

    private fun deleteMessage() {
        client.deleteMessage("YoungShatnerusenameinsteadplz-25f432a5-1efb-4272-9bbd-b7d0994118c8")
            .enqueue { result ->
                // Check result and show message
            }
    }

    private fun sendFile(file: File) {
        client.sendFile(
            channelId = channel.id,
            channelType = channel.type,
            file = file,
            mimeType = "image"         //"file"
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun sendFileProgress(file: File) {
        client.sendFile(
            channelId = channel.id,
            channelType = channel.type,
            file = file,
            mimeType = "image",         //"file"
            callback = object : ProgressCallback {
                override fun onError(error: ChatError) {
                    // Error on sending file
                }

                override fun onProgress(progress: Long) {
                    // Progress
                }

                override fun onSuccess(file: String) {
                    // File load completed
                }

            }
        )
    }

    private fun deleteFile() {
        client.deleteFile(
            channelId = channel.id,
            channelType = channel.type,
            url = "https://giphy.com/gifs/SJk9xTbxcg0DFDs89d"
        ).enqueue { result ->
            // Check result and show message
        }
    }

    private fun deleteImage() {
        client.deleteImage(
            channelId = channel.id,
            channelType = channel.type,
            url = "https://giphy.com/gifs/SJk9xTbxcg0DFDs89d"
        ).enqueue { result ->
            // Check result and show message
        }
    }

}
