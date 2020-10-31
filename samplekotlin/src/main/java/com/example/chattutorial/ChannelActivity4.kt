package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chattutorial.databinding.ActivityChannel4Binding
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
import io.getstream.chat.android.client.models.Channel

class ChannelActivity4 : AppCompatActivity() {

    private lateinit var binding: ActivityChannel4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChannel4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity"
        }

        // Step 1 - Create 3 separate ViewModels for the views so it's easy to customize one of the components
        val factory = ChannelViewModelFactory(cid)
        val channelHeaderViewModel: ChannelHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        // Set custom AttachmentViewHolderFactory
        binding.messageListView.setAttachmentViewHolderFactory(MyAttachmentViewHolderFactory())

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        channelHeaderViewModel.bindView(binding.channelHeaderView, this)
        messageListViewModel.bindView(binding.messageListView, this)
        messageInputViewModel.bindView(binding.messageInputView, this)

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
        binding.messageListView.setOnMessageEditHandler {
            messageInputViewModel.editMessage.postValue(it)
        }

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        binding.channelHeaderView.onBackClick = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        onBackPressedDispatcher.addCallback(this) {
            binding.channelHeaderView.onBackClick()
        }

        // Custom typing info header bar
        val nobodyTyping = "nobody is typing"
        binding.typingHeader.text = nobodyTyping

        val currentlyTyping = mutableSetOf<String>()

        ChatClient
            .instance()
            .channel(cid)
            .subscribeFor(this, TypingStartEvent::class, TypingStopEvent::class
            ) { event ->
                when (event) {
                    is TypingStartEvent -> currentlyTyping.add(event.user.name)
                    is TypingStopEvent -> currentlyTyping.remove(event.user.name)
                }

                binding.typingHeader.text = when {
                    currentlyTyping.isNotEmpty() -> currentlyTyping.joinToString(prefix = "typing: ")
                    else -> nobodyTyping
                }
            }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity4::class.java).putExtra(CID_KEY, channel.cid)
    }
}
