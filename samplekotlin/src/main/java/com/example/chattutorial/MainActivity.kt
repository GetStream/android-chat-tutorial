package com.example.chattutorial

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chattutorial.databinding.ActivityMainBinding
import com.getstream.sdk.chat.ChatUI
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Step 1 - Set up the client for API calls, the domain for offline storage and the UI components
        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        ChatDomain.Builder(client, applicationContext).build()
        ChatUI.Builder(applicationContext).build()

        // Step 2 - Authenticate and connect the user
        val user = User(
            id = "summer-brook-2",
            extraData = mutableMapOf(
                "name" to "Paranoid Android",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )
        client.connectUser(
            user = user,
            token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0"
        ).enqueue()

        // Step 3 - Set the channel list filter and order
        // This can be read as requiring only channels whose "type" is "messaging" AND
        // whose "members" include our "user.id"
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(user.id))
        )
        val viewModelFactory = ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
        val viewModel: ChannelListViewModel by viewModels { viewModelFactory }

        // Step 4 - Connect the ChannelListViewModel to the ChannelListView, loose coupling makes it easy to customize
        viewModel.bindView(binding.channelListView, this)
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity4.newIntent(this, channel))
        }
    }
}
