package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import kotlinx.android.synthetic.main.activity_channel_4.*

class ChannelActivity4 : AppCompatActivity(R.layout.activity_channel_4) {

    private val cid: String by lazy {
        intent.getStringExtra(CID_KEY)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelProvider = ViewModelProvider(this, ChannelViewModelsFactory(cid))

        messageListView.setAttachmentViewHolderFactory(MyAttachmentViewHolderFactory())
        val messagesViewModel = viewModelProvider.get(MessageListViewModel::class.java)
            .apply {
                bindView(messageListView, this@ChannelActivity4)
                state.observe(
                    this@ChannelActivity4
                )
                {
                    when (it) {
                        is MessageListViewModel.State.Loading -> progressBar.visible(true)
                        is MessageListViewModel.State.Result -> progressBar.visible(false)
                        is MessageListViewModel.State.NavigateUp -> finish()
                    }
                }
            }

        val channelController = ChatClient.instance().channel(cid)
        val currentlyTyping = MutableLiveData<Set<String>>(emptySet())

        channelController.subscribe {
            val typing = currentlyTyping.value ?: emptySet()
            val typingCopy: MutableSet<String> = typing.toMutableSet()
            when (it) {
                is TypingStartEvent -> {
                    val name = it.user.extraData["name"] as String
                    typingCopy += name
                    currentlyTyping.postValue(typingCopy)
                }
                is TypingStopEvent -> {
                    val name = it.user.extraData["name"] as String
                    typingCopy -= name
                    currentlyTyping.postValue(typingCopy)
                }
            }
        }

        val typingObserver = Observer<Set<String>> { users ->
            var typing = "nobody is typing"
            if (users.isNotEmpty()) {
                typing = "typing: " + users.joinToString(", ")
            }
            channelHeaderView.text = typing
        }
        currentlyTyping.observe(this, typingObserver)

        viewModelProvider.get(MessageInputViewModel::class.java).apply {
            bindView(messageInputView, this@ChannelActivity4)
            messagesViewModel.mode.observe(
                this@ChannelActivity4
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

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel) =
            Intent(context, ChannelActivity4::class.java).apply {
                putExtra(CID_KEY, channel.cid)
            }
    }
}
