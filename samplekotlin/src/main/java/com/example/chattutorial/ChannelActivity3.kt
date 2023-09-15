package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chattutorial.databinding.ActivityChannel3Binding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.state.extensions.watchChannelAsState
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.messages.bindView
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChannelActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityChannel3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityChannel3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity3"
        }

        // Step 1 - Create three separate ViewModels for the views so it's easy
        //          to customize them individually
        val factory = MessageListViewModelFactory(this, cid)
        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageComposerViewModel: MessageComposerViewModel by viewModels { factory }

        // Set a view factory manager for Imgur attachments
        val imgurAttachmentViewFactory = ImgurAttachmentFactory()
        val attachmentViewFactory = AttachmentFactoryManager(listOf(imgurAttachmentViewFactory))
        binding.messageListView.setAttachmentFactoryManager(attachmentViewFactory)

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        messageListHeaderViewModel.bindView(binding.messageListHeaderView, this)
        messageListViewModel.bindView(binding.messageListView, this)
        messageComposerViewModel.bindView(binding.messageComposerView, this)

        // Step 3 - Let both MessageListHeaderView and MessageComposerView know when we open a thread
        messageListViewModel.mode.observe(this) { mode ->
            when (mode) {
                is MessageMode.MessageThread -> {
                    messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                    messageComposerViewModel.setMessageMode(MessageMode.MessageThread(mode.parentMessage))
                }

                is MessageMode.Normal -> {
                    messageListHeaderViewModel.resetThread()
                    messageComposerViewModel.leaveThread()
                }
            }
        }

        // Step 4 - Let the message input know when we are editing a message
        binding.messageListView.setMessageEditHandler { message ->
            messageComposerViewModel.performMessageAction(Edit(message))
        }

        // Step 5 - Handle navigate up state
        messageListViewModel.state.observe(this) { state ->
            if (state is MessageListViewModel.State.NavigateUp) {
                finish()
            }
        }

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.messageListHeaderView.setBackButtonClickListener(backHandler)
        onBackPressedDispatcher.addCallback(this) {
            backHandler()
        }

        // Custom typing info header bar
        val nobodyTyping = "nobody is typing"
        binding.typingHeaderView.text = nobodyTyping

        // Observe typing events and update typing header depending on its state.

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ChatClient.instance().watchChannelAsState(cid, 30)
                    .filterNotNull()
                    .flatMapLatest { it.typing }
                    .collect {
                        binding.typingHeaderView.text = when {
                            it.users.isNotEmpty() -> it.users.joinToString(prefix = "typing: ") { user -> user.name }
                            else -> nobodyTyping
                        }
                    }
            }
        }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity3::class.java).putExtra(CID_KEY, channel.cid)
    }
}
