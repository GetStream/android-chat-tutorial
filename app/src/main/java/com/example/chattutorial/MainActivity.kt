package com.example.chattutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chattutorial.databinding.ActivityMainBinding
import com.getstream.sdk.chat.StreamChat
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel
import io.getstream.chat.android.client.models.Filters.`in`
import io.getstream.chat.android.client.models.Filters.and
import io.getstream.chat.android.client.models.Filters.eq
import io.getstream.chat.android.client.models.User

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // setup the client using the example API key
        // normally you would call init in your Application class and not the activity
        StreamChat.init(StreamChat.Config("2mhcpx4yke7x", this.applicationContext))
        val client = StreamChat.getInstance()
        val extraData = HashMap<String, Any>()
        extraData["name"] = "Paranoid Android"
        extraData["image"] = "https://bit.ly/2TIt8NR"
        val currentUser = User("weathered-dust-6")
        // User token is typically provided by your server when the user authenticates
        client.setUser(
            currentUser,
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoid2VhdGhlcmVkLWR1c3QtNiJ9.BkWDEi7suYpXBpJQs8ddzKd6sKJJSmS1_5vZkA70_FE"
        )

        // we're using data binding in this example
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this

        // most the business logic for chat is handled in the ChannelListViewModel view model
        val viewModel = ViewModelProvider(this).get(ChannelListViewModel::class.java)

        binding.viewModel = viewModel
        binding.channelList.setViewModel(viewModel, this)

        // query all channels of type messaging
        val filter = and(eq("type", "messaging"), `in`("members", "weathered-dust-6"))
        viewModel.setChannelFilter(filter)

        // click handlers for clicking a user avatar or channel
        binding.channelList.setOnChannelClickListener { channel ->
            // open the channel activity
        }
        binding.channelList.setOnUserClickListener { user ->
            // open your user profile
        }

    }
}