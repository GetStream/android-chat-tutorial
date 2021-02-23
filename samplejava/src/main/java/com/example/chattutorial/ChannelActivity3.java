package com.example.chattutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chattutorial.databinding.ActivityChannel3Binding;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;

import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.livedata.controller.ChannelController;
import io.getstream.chat.android.ui.message.input.viewmodel.MessageInputViewModelBinding;
import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;

public class ChannelActivity3 extends AppCompatActivity {

    private final static String CID_KEY = "key:cid";

    public static Intent newIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelActivity3.class);
        intent.putExtra(CID_KEY, channel.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Step 0 - inflate binding
        ActivityChannel3Binding binding = ActivityChannel3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity3");
        }

        // Step 1 - Create three separate ViewModels for the views so it's easy
        //          to customize them individually
        MessageListViewModelFactory factory = new MessageListViewModelFactory(cid);
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageInputViewModel messageInputViewModel = provider.get(MessageInputViewModel.class);

        // Set custom AttachmentViewHolderFactory
        binding.messageListView.setMessageViewHolderFactory(new ImgurAttachmentViewHolderFactory());

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this);
        MessageInputViewModelBinding.bind(messageInputViewModel, binding.messageInputView, this);

        // Step 3 - Let both MessageListHeaderView and MessageInputView know when we open a thread
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof Thread) {
                Message parentMessage = ((Thread) mode).getParentMessage();
                messageListHeaderViewModel.setActiveThread(parentMessage);
                messageInputViewModel.setActiveThread(parentMessage);
            } else if (mode instanceof Normal) {
                messageListHeaderViewModel.resetThread();
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
        binding.messageListView.setMessageEditHandler(message -> {
            messageInputViewModel.getEditMessage().postValue(message);
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessageListHeaderView.OnClickListener backHandler = () -> {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
        };
        binding.messageListHeaderView.setBackButtonClickListener(backHandler);
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

        // Obtain a ChannelController
        ChatDomain.instance()
                .getUseCases()
                .getGetChannelController()
                .invoke(cid)
                .enqueue((result) -> {
                    ChannelController channelController = result.data();

                    // Observe typing users
                    channelController.getTyping().observe(this, typingState -> {
                        final List<User> users = typingState.getUsers();
                        if (users.isEmpty()) {
                            typingHeaderView.setText(nobodyTyping);
                        } else {
                            List<String> userNames = new ArrayList<>(users.size());
                            for (User user : users) {
                                userNames.add((String) user.getExtraData().get("name"));
                            }
                            String typing = "typing: " + TextUtils.join(", ", userNames);
                            typingHeaderView.setText(typing);
                        }
                    });
                });
    }
}
