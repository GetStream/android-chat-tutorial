package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
<<<<<<< HEAD
=======
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.controllers.subscribeFor
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
>>>>>>> 23067e524310df9e383d921476e1f7625c2b04df
import io.getstream.chat.android.client.models.Channel
<<<<<<< HEAD
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.livedata.ChatDomain
import kotlinx.android.synthetic.main.activity_channel_4.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel_4.messageInputView
import kotlinx.android.synthetic.main.activity_channel_4.messageListView
import kotlinx.android.synthetic.main.activity_channel_4.progressBar
=======
import kotlinx.android.synthetic.main.activity_channel_3.channelHeaderView
import kotlinx.android.synthetic.main.activity_channel_3.messageInputView
import kotlinx.android.synthetic.main.activity_channel_3.messageListView
import kotlinx.android.synthetic.main.activity_channel_3.progressBar
>>>>>>> f712e5c8a3825e1f25e13b80da4cfcc899d81e0a

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
                        is MessageListViewModel.State.Loading -> progressBar.visibility = View.VISIBLE
                        is MessageListViewModel.State.Result -> progressBar.visibility = View.GONE
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
