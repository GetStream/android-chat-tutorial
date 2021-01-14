package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chattutorial.databinding.ActivityChannel2Binding
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemViewHolderFactory
import io.getstream.chat.android.ui.messages.header.bindView
import io.getstream.chat.android.ui.messages.view.bindView
import io.getstream.chat.android.ui.textinput.bindView

class ChannelActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityChannel2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChannel2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity2"
        }

        // Step 1 - Create 3 separate ViewModels for the views so it's easy to customize one of the components
        val factory = ChannelViewModelFactory(cid)
        val channelHeaderViewModel: ChannelHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        // Set view holder factory for Imgur attachments
        binding.messageListView.setMessageViewHolderFactory(
            object : MessageListItemViewHolderFactory() {
                val IMGUR = 999
                override fun getViewType(item: MessageListItem): Int {
                    return when (item) {
                        is MessageListItem.MessageItem -> {
                            item.message
                                .attachments
                                .firstOrNull()
                                ?.imageUrl
                                ?.contains("imgur")
                                ?.let { IMGUR }
                                ?: super.getViewType(item)
                        }

                        else -> super.getViewType(item)
                    }
                }

                override fun createViewHolder(
                    parentView: ViewGroup,
                    viewType: Int
                ): BaseMessageItemViewHolder<out MessageListItem> {
                    return when (viewType) {
                        IMGUR -> createImgurViewHolder(parentView)
                        else -> super.createViewHolder(parentView, viewType)
                    }
                }

                fun createImgurViewHolder(parent: ViewGroup): BaseMessageItemViewHolder<out MessageListItem> {
                    return ImgurAttachmentViewHolder(parent)
                }
            }
        )

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
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.channelHeaderView.setBackButtonClickListener(backHandler)
        onBackPressedDispatcher.addCallback(this) {
            backHandler()
        }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity2::class.java).putExtra(CID_KEY, channel.cid)
    }
}
