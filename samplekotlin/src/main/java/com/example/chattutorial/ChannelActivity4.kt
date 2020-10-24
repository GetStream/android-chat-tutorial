package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
import io.getstream.chat.android.client.models.Channel
import kotlinx.android.synthetic.main.activity_channel.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel.messageInputView
import kotlinx.android.synthetic.main.activity_channel.messageListView
import kotlinx.android.synthetic.main.activity_channel_3.*


class ChannelActivity4 : AppCompatActivity(R.layout.activity_channel_4) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {"Specifying a channel id is required when starting ChannelActivity"}

        // step 1 - we create 3 separate ViewModels for the views so it's easy to customize one of the components
        val viewModelProvider = ViewModelProvider(this, ChannelViewModelFactory(cid))
        val channelHeaderViewModel = viewModelProvider.get(ChannelHeaderViewModel::class.java)
        val messageListViewModel = viewModelProvider.get(MessageListViewModel::class.java)
        val messageInputViewModel = viewModelProvider.get(MessageInputViewModel::class.java)

        // set custom AttachmentViewHolderFactory
        messageListView.setAttachmentViewHolderFactory(MyAttachmentViewHolderFactory())

        // step 2 = we bind the view and ViewModels. they are loosely coupled so its easy to customize
        channelHeaderViewModel.bindView(channelHeaderView, this)
        messageListViewModel.bindView(messageListView, this)
        messageInputViewModel.bindView(messageInputView, this)

        // step 3 - let the message input know when we open a thread
        messageListViewModel.mode.observe(this) {
            when (it) {
                is MessageListViewModel.Mode.Thread -> messageInputViewModel.setActiveThread(it.parentMessage)
                is MessageListViewModel.Mode.Normal -> messageInputViewModel.resetThread()
            }
        }
        // step 4 - let the message input know when we are editing a message
        messageListView.setOnMessageEditHandler {
            messageInputViewModel.editMessage.postValue(it)
        }

        // custom typing info header bar
        val channelController = ChatClient.instance().channel(cid)
        val currentlyTyping = MutableLiveData<Set<String>>(emptySet())

        channelController.subscribeFor(TypingStartEvent::class.java, TypingStopEvent::class.java) {
            val typing = currentlyTyping.value ?: emptySet()
            val typingCopy: MutableSet<String> = typing.toMutableSet()
            when (it) {
                is TypingStartEvent -> {
                    val name = it.user.extraData["name"] as String
                    typingCopy += name
                }
                is TypingStopEvent -> {
                    val name = it.user.extraData["name"] as String
                    typingCopy -= name
                }
            }
            currentlyTyping.postValue(typingCopy)
        }

        val typingObserver = Observer<Set<String>> { users ->
            channelHeaderSub.text = if (users.isEmpty()) {
                "nobody is typing"
            } else {
                "typing: " + users.joinToString(", ")
            }
        }
        currentlyTyping.observe(this, typingObserver)

        // step 5 - handle back button behaviour correctly when you're in a thread
        val backButtonHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        channelHeaderView.onBackClick = { backButtonHandler() }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backButtonHandler()
                }
            }
        )
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel) =
            Intent(context, ChannelActivity::class.java).apply {
                putExtra(CID_KEY, channel.cid)
            }
    }
}
