package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChatViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.models.Channel
import kotlinx.android.synthetic.main.activity_channel.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel.messageInputView
import kotlinx.android.synthetic.main.activity_channel.messageListView
import kotlinx.android.synthetic.main.activity_channel.progressBar

class ChannelActivity : AppCompatActivity(R.layout.activity_channel) {

    private val cid: String by lazy {
        intent.getStringExtra(CID_KEY)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // we use 3 separate ViewModels for the views so it's easy to customize one of the components
        val viewModelProvider = ViewModelProvider(this, ChatViewModelFactory(cid))
        val channelHeaderViewModel = viewModelProvider.get(ChannelHeaderViewModel::class.java)
        val messageListViewModel = viewModelProvider.get(MessageListViewModel::class.java)
        val messageInputViewModel = viewModelProvider.get(MessageInputViewModel::class.java)

        // next we bind the view and ViewModels. they are loosely coupled so its easy to customize
        channelHeaderViewModel.bindView(channelHeaderView, this)
        messageListViewModel.bindView(messageListView, this)
        messageInputViewModel.bindView(messageInputView, this)

        // TODO set custom AttachmentViewHolderFactory

        // connect the message list to the message input
        messageListViewModel.mode.observe(this) {
            when (it) {
                is MessageListViewModel.Mode.Thread -> messageInputViewModel.setActiveThread(it.parentMessage)
                is MessageListViewModel.Mode.Normal -> messageInputViewModel.resetThread()
            }
        }
        // handle the edit interface for messages in the message input
        messageListView.setOnMessageEditHandler {
            messageInputViewModel.editMessage.postValue(it)
        }

        // TODO: any way to simplify the code below?
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
