package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chattutorial.databinding.ActivityChannel3Binding
import com.getstream.sdk.chat.StreamChat
import com.getstream.sdk.chat.model.Channel
import com.getstream.sdk.chat.utils.PermissionChecker
import com.getstream.sdk.chat.view.MessageInputView.PermissionRequestListener
import com.getstream.sdk.chat.viewmodel.ChannelViewModel
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
import java.util.*

/**
 * Show the messages for a channel
 *
 */
class ChannelActivity3 : AppCompatActivity(), PermissionRequestListener {

    private lateinit var binding: ActivityChannel3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // receive the intent and create a channel object
        val intent = intent
        val channelType = intent.getStringExtra(EXTRA_CHANNEL_TYPE)
        val channelID = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val client = StreamChat.getInstance()

        // we're using data binding in this example
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel_3)
        // most the business logic of the chat is handled in the ChannelViewModel view model
        binding.lifecycleOwner = this

        val viewModelFactory = ChannelViewModelFactory(application, channelType, channelID)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ChannelViewModel::class.java)

        viewModel.initializedState.observe(this, Observer {
            // connect the view model
            val factory = MyMessageViewHolderFactory()
            binding.messageList.setViewHolderFactory(factory)
            binding.viewModel = viewModel
            binding.messageList.setViewModel(viewModel, this)
            binding.messageInput.setViewModel(viewModel, this)

            val currentlyTyping = MutableLiveData<List<String>>(ArrayList())

            StreamChat.getInstance().events().subscribe { event ->
                if (event is TypingStartEvent) {
                    val typing = currentlyTyping.value ?: listOf()
                    val typingCopy: MutableList<String> = typing.toMutableList()
                    val user = event.user!!
                    if (typingCopy.contains(user.name).not()) {
                        typingCopy.add(user.name)
                    }
                    currentlyTyping.postValue(typingCopy)
                } else if (event is TypingStopEvent) {
                    val typing = currentlyTyping.value ?: listOf()
                    val typingCopy: MutableList<String> = typing.toMutableList()
                    val user = event.user!!
                    typingCopy.remove(user.name)
                    currentlyTyping.postValue(typingCopy)
                }
            }

            val typingObserver = Observer<List<String>> { users ->
                var typing = "nobody is typing"
                if (users.isNotEmpty()) {
                    typing = "typing: " + users.joinToString(", ")
                }
                binding.typing = typing
            }
            currentlyTyping.observe(this, typingObserver)

            // If you are using own MessageInputView please comment this line.
            binding.messageInput.setPermissionRequestListener(this)
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // If you are using own MessageInputView please comment this line.
        binding.messageInput.captureMedia(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) { // If you are using own MessageInputView please comment this line.
        binding.messageInput.permissionResult(requestCode, permissions, grantResults)
    }

    override fun openPermissionRequest() {
        PermissionChecker.permissionCheck(this, null)
        // If you are writing a Channel Screen in a Fragment, use the code below instead of the code above.
        // PermissionChecker.permissionCheck(getActivity(), this);
    }

    companion object {

        private const val EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE"
        private const val EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID"

        fun newIntent(context: Context, channel: Channel): Intent {
            val intent = Intent(context, ChannelActivity3::class.java)
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.type)
            intent.putExtra(EXTRA_CHANNEL_ID, channel.id)
            return intent
        }
    }

}