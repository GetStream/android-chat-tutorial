package com.example.chattutorialjava;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.getstream.sdk.chat.Chat;
import com.getstream.sdk.chat.view.channels.ChannelsView;
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModelBinding;
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModel;
import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModelImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import io.getstream.chat.android.client.errors.ChatError;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.logger.ChatLogger;
import io.getstream.chat.android.client.logger.ChatLogger.Config;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.socket.InitConnectionListener;
import kotlin.Unit;

public final class MainActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Chat.Builder("b67pax5b2wdq", getApplicationContext()).build();
        new ChatLogger.Builder(new Config(ChatLogLevel.ALL, null)).build();

        User user = new User();
        user.setId("summer-brook-2");
        user.getExtraData().put("name", "Paranoid Android");
        user.getExtraData().put("image", "https://bit.ly/2TIt8NR");

        Chat.getInstance().setUser(user, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0", new InitConnectionListener() {
            public void onSuccess(@NotNull ConnectionData data) {
                Log.i("MainActivity", "setUser completed");
            }

            public void onError(@NotNull ChatError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "setUser onError");
            }
        });
        final ChannelsViewModel channelsViewModel = new ViewModelProvider(this).get(ChannelsViewModelImpl.class);

        final ChannelsView channelsView = findViewById(R.id.channelsView);
        channelsView.setOnChannelClickListener((channel -> {
            startActivity(ChannelActivity.newIntent(this, channel));
            return Unit.INSTANCE;
        }));
        ChannelsViewModelBinding.bind(channelsViewModel, channelsView, this);
    }
}

