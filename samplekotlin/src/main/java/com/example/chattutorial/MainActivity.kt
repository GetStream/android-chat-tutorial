package com.example.chattutorial

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chattutorial.databinding.ActivityMainBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding for layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarInsetsAsPadding()

        // Set up the client for API calls with offline storage and state management
        val client = ChatClient.Builder("uun7ywwamhs9", applicationContext)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

        // Authenticate and connect the user
        val user = User(
            id = "tutorial-droid",
            name = "Tutorial Droid",
            image = "https://bit.ly/2TIt8NR"
        )
        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.WwfBzU1GZr0brt_fXnqKdKhz3oj0rbDUm2DqJO_SS5U"
        ).enqueue {
            if (it.isSuccess) {
                // Set the channel list filter and order
                // This can be read as requiring only channels whose "type" is "messaging" AND
                // whose "members" include our "user.id"
                val filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(user.id))
                )
                val viewModelFactory =
                    ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
                val viewModel: ChannelListViewModel by viewModels { viewModelFactory }

                // Connect the ChannelListViewModel to the ChannelListView, loose
                // coupling makes it easy to customize
                viewModel.bindView(binding.channelListView, this)
                binding.channelListView.setChannelItemClickListener { channel ->
                    startActivity(ChannelActivity4.newIntent(this, channel))
                }
            } else {
                Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
