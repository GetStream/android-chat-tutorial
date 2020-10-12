package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.view.common.visible
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
import io.getstream.chat.android.client.models.Channel
import kotlinx.android.synthetic.main.activity_channel_3.*

class ChannelActivity3 : AppCompatActivity(R.layout.activity_channel_3) {

    private val cid: String by lazy {
        intent.getStringExtra(CID_KEY)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelProvider = ViewModelProvider(this, ChannelViewModelsFactory(cid))

        messageListView.setAttachmentViewHolderFactory(MyAttachmentViewHolderFactory())
        val messagesViewModel = viewModelProvider.get(MessageListViewModel::class.java)
            .apply {
                bindView(messageListView, this@ChannelActivity3)
                state.observe(
                    this@ChannelActivity3
                )
                {
                    when (it) {
                        is MessageListViewModel.State.Loading -> progressBar.visible(true)
                        is MessageListViewModel.State.Result -> progressBar.visible(false)
                        is MessageListViewModel.State.NavigateUp -> finish()
                    }
                }
            }

        channelHeaderView.text = "nobody is typing"
        val currentlyTyping = mutableSetOf<String>()
        ChatClient.instance().channel(cid).subscribe { event ->
            when (event) {
                is TypingStartEvent -> event.user.extraData["name"]?.let {
                    currentlyTyping.add(it as String)
                    updateChannelHeaderView(currentlyTyping)
                }
                is TypingStopEvent -> event.user.extraData["name"]?.let {
                    currentlyTyping.remove(it as String)
                    updateChannelHeaderView(currentlyTyping)
                }
            }
        }

        viewModelProvider.get(MessageInputViewModel::class.java).apply {
            bindView(messageInputView, this@ChannelActivity3)
            messagesViewModel.mode.observe(
                this@ChannelActivity3
            ) {
                when (it) {
                    is MessageListViewModel.Mode.Thread -> setActiveThread(it.parentMessage)
                    is MessageListViewModel.Mode.Normal -> resetThread()
                }
            }
            messageListView.setOnMessageEditHandler {
                editMessage.postValue(it)
            }
        }
        val backButtonHandler = {
            messagesViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backButtonHandler()
                }
            }
        )
    }

    private fun updateChannelHeaderView(currentlyTyping: Set<String>) {
        channelHeaderView.text = when {
            currentlyTyping.isNotEmpty() -> "typing: ${currentlyTyping.joinToString()}"
            else -> "nobody is typing"
        }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel) =
            Intent(context, ChannelActivity3::class.java).apply {
                putExtra(CID_KEY, channel.cid)
            }
    }
}
