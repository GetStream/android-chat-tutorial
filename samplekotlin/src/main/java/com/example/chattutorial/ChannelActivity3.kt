package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.livedata.controller.ChannelController
import kotlinx.android.synthetic.main.activity_channel.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel.messageInputView
import kotlinx.android.synthetic.main.activity_channel.messageListView
import kotlinx.android.synthetic.main.activity_channel_3.typingHeader

class ChannelActivity3 : AppCompatActivity(R.layout.activity_channel_3) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity3"
        }

        // Step 1 - Create 3 separate ViewModels for the views so it's easy to customize one of the components
        val factory = ChannelViewModelFactory(cid)
        val channelHeaderViewModel: ChannelHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        // Set custom AttachmentViewHolderFactory
        messageListView.setAttachmentViewHolderFactory(MyAttachmentViewHolderFactory())

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        channelHeaderViewModel.bindView(channelHeaderView, this)
        messageListViewModel.bindView(messageListView, this)
        messageInputViewModel.bindView(messageInputView, this)

        // Step 3 - Let the message input know when we open a thread
        messageListViewModel.mode.observe(this) { mode ->
            when (mode) {
                is Thread -> messageInputViewModel.setActiveThread(mode.parentMessage)
                is Normal -> messageInputViewModel.resetThread()
            }
        }

        // Step 4 - Handle navigate up state
        messageListViewModel.state.observe(this) { state ->
            if (state is NavigateUp) {
                finish()
            }
        }

        // Step 5 - Let the message input know when we are editing a message
        messageListView.setOnMessageEditHandler {
            messageInputViewModel.editMessage.postValue(it)
        }

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        channelHeaderView.onBackClick = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        onBackPressedDispatcher.addCallback(this) {
            channelHeaderView.onBackClick()
        }

        // Custom typing info header bar
        val nobodyTyping = "nobody is typing"
        typingHeader.text = nobodyTyping

        // Obtain a ChannelController
        // TODO replace watchChannel with getChannelController
        val channelController: ChannelController =
            ChatDomain.instance().useCases.watchChannel(cid, 0).execute().data()

        // Observe typing users
        channelController.typing.observe(this) { users ->
            typingHeader.text = when {
                users.isNotEmpty() -> users.joinToString(prefix = "typing: ") { user -> user.name }
                else -> nobodyTyping
            }
        }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity3::class.java).putExtra(CID_KEY, channel.cid)
    }
}
