package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.view.common.visible
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.livedata.ChatDomain
import kotlinx.android.synthetic.main.activity_channel_4.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel_4.messageInputView
import kotlinx.android.synthetic.main.activity_channel_4.messageListView
import kotlinx.android.synthetic.main.activity_channel_4.progressBar

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

        val nobodyTypingText = "nobody is typing"
        channelHeaderView.text = nobodyTypingText
        val typingObserver = Observer<List<User>> { users ->
            channelHeaderView.text = if (users.isNotEmpty()) {
                "typing: " + users.joinToString(", ") { it.name }
            } else {
                nobodyTypingText
            }
        }

        ChatDomain.instance().useCases.watchChannel(cid, messageLimit = 30).enqueue {
            if (it.isSuccess) {
                val channelController = it.data()
                runOnUiThread {
                    channelController.typing.observe(this, typingObserver)
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

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel) =
            Intent(context, ChannelActivity3::class.java).apply {
                putExtra(CID_KEY, channel.cid)
            }
    }
}
