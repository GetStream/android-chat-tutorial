package com.example.chattutorialjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;

import java.util.HashSet;
import java.util.Set;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.events.TypingStartEvent;
import io.getstream.chat.android.client.events.TypingStopEvent;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.ui.messages.header.ChannelHeaderViewModelBinding;
import io.getstream.chat.android.ui.messages.header.MessagesHeaderView;
import io.getstream.chat.android.ui.messages.view.MessageListView;
import io.getstream.chat.android.ui.messages.view.MessageListViewModelBinding;
import io.getstream.chat.android.ui.textinput.MessageInputView;
import io.getstream.chat.android.ui.textinput.MessageInputViewModelBinding;
import kotlin.Unit;

public class ChannelActivity4 extends AppCompatActivity {

    private final static String CID_KEY = "key:cid";

    public static Intent newIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelActivity4.class);
        intent.putExtra(CID_KEY, channel.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_4);
        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity4");
        }

        // Step 0 - Get View references
        MessageListView messageListView = findViewById(R.id.messageListView);
        MessagesHeaderView channelHeaderView = findViewById(R.id.channelHeaderView);
        MessageInputView messageInputView = findViewById(R.id.messageInputView);

        // Step 1 - Create 3 separate ViewModels for the views so it's easy to customize one of the components
        ChannelViewModelFactory factory = new ChannelViewModelFactory(cid);
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        ChannelHeaderViewModel channelHeaderViewModel = provider.get(ChannelHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageInputViewModel messageInputViewModel = provider.get(MessageInputViewModel.class);

        // Set custom AttachmentViewHolderFactory
        messageListView.setMessageViewHolderFactory(new ImgurAttachmentViewHolderFactory());

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        ChannelHeaderViewModelBinding.bind(channelHeaderViewModel, channelHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, messageListView, this);
        MessageInputViewModelBinding.bind(messageInputViewModel, messageInputView, this);

        // Step 3 - Let the message input know when we open a thread
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof Thread) {
                messageInputViewModel.setActiveThread(((Thread) mode).getParentMessage());
            } else if (mode instanceof Normal) {
                messageInputViewModel.resetThread();
            }
        });

        // Step 4 - Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
        });

        // Step 5 - Let the message input know when we are editing a message
        messageListView.setOnMessageEditHandler(message -> {
            messageInputViewModel.getEditMessage().postValue(message);
            return Unit.INSTANCE;
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessagesHeaderView.OnClickListener backHandler = () -> {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
        };
        channelHeaderView.setBackButtonClickListener(backHandler);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backHandler.onClick();
            }
        });

        // Custom typing info header bar
        TextView typingHeaderView = findViewById(R.id.typingHeaderView);
        String nobodyTyping = "nobody is typing";
        typingHeaderView.setText(nobodyTyping);

        Set<String> currentlyTyping = new HashSet<>();
        ChatClient
                .instance()
                .channel(cid)
                .subscribeFor(
                        this,
                        new Class[]{TypingStartEvent.class, TypingStopEvent.class}, event -> {
                            if (event instanceof TypingStartEvent) {
                                User user = ((TypingStartEvent) event).getUser();
                                String name = (String) user.getExtraData().get("name");
                                currentlyTyping.add(name);
                            } else if (event instanceof TypingStopEvent) {
                                User user = ((TypingStopEvent) event).getUser();
                                String name = (String) user.getExtraData().get("name");
                                currentlyTyping.remove(name);
                            }

                            String typing = "nobody is typing";
                            if (!currentlyTyping.isEmpty()) {
                                typing = "typing: " + TextUtils.join(", ", currentlyTyping);
                            }
                            typingHeaderView.setText(typing);
                            return Unit.INSTANCE;
                        });
    }
}
