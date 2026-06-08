package com.example.chattutorial;

import static java.util.Collections.singletonList;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chattutorial.databinding.ActivityMainBinding;

import org.jetbrains.annotations.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.models.FilterObject;
import io.getstream.chat.android.models.Filters;
import io.getstream.chat.android.models.User;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory;

public final class MainActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate binding for layout
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.applySystemBarInsetsAsPadding(binding.getRoot());

        // Set up the client for API calls with offline storage and state management
        ChatClient client = new ChatClient.Builder("uun7ywwamhs9", getApplicationContext())
                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
                .build();

        // Authenticate and connect the user
        User user = new User.Builder()
                .withId("tutorial-droid")
                .withName("Tutorial Droid")
                .withImage("https://bit.ly/2TIt8NR")
                .build();

        client.connectUser(
                user,
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.WwfBzU1GZr0brt_fXnqKdKhz3oj0rbDUm2DqJO_SS5U"
        ).enqueue(result -> {
            // Set the channel list filter and order
            // This can be read as requiring only channels whose "type" is "messaging" AND
            // whose "members" include our "user.id"
            FilterObject filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.in("members", singletonList(user.getId()))
            );

            ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
                    .filter(filter)
                    .sort(ChannelListViewModel.DEFAULT_SORT)
                    .build();

            ChannelListViewModel channelsViewModel =
                    new ViewModelProvider(this, factory).get(ChannelListViewModel.class);

            // Connect the ChannelListViewModel to the ChannelListView, loose
            // coupling makes it easy to customize
            ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
            binding.channelListView.setChannelItemClickListener(
                    channel -> startActivity(ChannelActivity4.newIntent(this, channel))
            );
        });
    }
}
