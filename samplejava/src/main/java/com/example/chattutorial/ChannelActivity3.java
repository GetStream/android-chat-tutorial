package com.example.chattutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.FlowLiveDataConversions;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.example.chattutorial.databinding.ActivityChannel3Binding;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;

import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.TypingEvent;
import io.getstream.chat.android.offline.extensions.ChatClientExtensions;
import io.getstream.chat.android.offline.plugin.state.channel.ChannelState;
import io.getstream.chat.android.ui.message.input.viewmodel.MessageInputViewModelBinding;
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.AttachmentFactoryManager;
import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;

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

        // Set view factory manager for Imgur attachments
        ImgurAttachmentFactory imgurAttachmentFactory = new ImgurAttachmentFactory();

        List<ImgurAttachmentFactory> imgurAttachmentViewFactories = new ArrayList<ImgurAttachmentFactory>();
        imgurAttachmentViewFactories.add(imgurAttachmentFactory);

        AttachmentFactoryManager attachmentFactoryManager = new AttachmentFactoryManager(imgurAttachmentViewFactories);
        binding.messageListView.setAttachmentFactoryManager(attachmentFactoryManager);

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this, true);
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

        // Step 4 - Let the message input know when we are editing a message
        binding.messageListView.setMessageEditHandler(messageInputViewModel::postMessageToEdit);

        // Step 5 - Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
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
        String nobodyTyping = "nobody is typing";
        binding.typingHeaderView.setText(nobodyTyping);

        // Observe typing events and update typing header depending on its state.
        Flow<ChannelState> channelStateFlow = ChatClientExtensions.watchChannelAsState(ChatClient.instance(), cid, 30);
        LiveData<TypingEvent> typingEventLiveData = Transformations.switchMap(
                FlowLiveDataConversions.asLiveData(channelStateFlow),
                channelState -> FlowLiveDataConversions.asLiveData(channelState.getTyping())
        );

        typingEventLiveData.observe(this, typingEvent -> {
            String headerText;

            if (typingEvent.getUsers().size() != 0) {
                headerText = "typing: " + joinTypingUpdatesToUserNames(typingEvent);
            } else {
                headerText = nobodyTyping;
            }

            binding.typingHeaderView.setText(headerText);
        });
    }

    // Helper method that transforms typing updates into a string
    // containing typing member's names
    @NonNull
    private String joinTypingUpdatesToUserNames(@NonNull TypingEvent typingEvent) {
        StringBuilder joinedString = new StringBuilder();

        for (int i = 0; i < typingEvent.getUsers().size(); i++) {
            if (i < typingEvent.getUsers().size() - 1) {
                joinedString.append(typingEvent.getUsers().get(i).getName()).append(", ");
            } else {
                joinedString.append(typingEvent.getUsers().get(i).getName());
            }
        }

        return joinedString.toString();
    }
}
