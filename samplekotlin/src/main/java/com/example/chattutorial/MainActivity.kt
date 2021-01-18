package com.example.chattutorial

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.common.databinding.ActivityMainBinding
import com.getstream.sdk.chat.ChatUI
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModel
import com.getstream.sdk.chat.viewmodel.factory.ChannelsViewModelFactory
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Step 1 - Set up the client for API calls, the domain for offline storage and the UI components
        val client =
            ChatClient.Builder("b67pax5b2wdq", applicationContext).logLevel(ChatLogLevel.ALL)
                .build()
        val domain = ChatDomain.Builder(client, applicationContext).build()
        ChatUI.Builder(client, domain, applicationContext).build()

        // Step 2 - Authenticate and connect the user
        val user = User("summer-brook-2").apply {
            extraData["name"] = "Paranoid Android"
            extraData["image"] = "https://bit.ly/2TIt8NR"
        }
        client.setUser(
            user = user,
            token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0"
        )

        // Step 3 - Set the channel list filter and order
        // This can be read as requiring only channels whose "type" is "messaging" AND
        // whose "members" include our "user.id"
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(user.id))
        )
        val viewModelFactory = ChannelsViewModelFactory(filter, ChannelsViewModel.DEFAULT_SORT)
        val viewModel: ChannelsViewModel by viewModels { viewModelFactory }

        // Step 4 - Connect the ChannelsViewModel to the ChannelsView, loose coupling makes it easy to customize
        viewModel.bindView(binding.channelsView, this)
        binding.channelsView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity2.newIntent(this, channel))
        }
    }
}
