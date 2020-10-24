package com.example.chattutorial

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.getstream.sdk.chat.Chat
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModel
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModelImpl
import com.getstream.sdk.chat.viewmodel.channels.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChannelsViewModelFactory
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.logger.ChatLogger
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.client.utils.FilterObject
import io.getstream.chat.android.livedata.ChatDomain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel by lazy {
        val filter = Filters.and(Filters.eq("type", "messaging"), Filters.`in`("members", listOf(ChatDomain.instance().currentUser.id)))
        val viewModelFactory = ChannelsViewModelFactory(filter, ChannelsViewModel.DEFAULT_SORT)
        ViewModelProvider(this, viewModelFactory).get(ChannelsViewModelImpl::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Chat.Builder(apiKey = "b67pax5b2wdq", context = applicationContext).build()
        ChatLogger.Builder(ChatLogger.Config(level = ChatLogLevel.ALL, handler = null)).build()

        val user = User("summer-brook-2").apply {
            extraData["name"] = "Paranoid Android"
            extraData["image"] = "https://bit.ly/2TIt8NR"
        }

        // User token is typically provided by your server when the user authenticates
        Chat.getInstance().setUser(
            user = user,
            token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0"
        )

        channelsView.setOnChannelClickListener { channel ->
            // open the channel activity
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        viewModel.bindView(channelsView, this)
    }
}