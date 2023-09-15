package com.example.chattutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.example.chattutorial.databinding.ActivityChannel3Binding;

import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.state.ChannelState;
import io.getstream.chat.android.client.extensions.FlowExtensions;
import io.getstream.chat.android.models.Channel;
import io.getstream.chat.android.models.Message;
import io.getstream.chat.android.models.TypingEvent;
import io.getstream.chat.android.state.extensions.ChatClientExtensions;
import io.getstream.chat.android.ui.common.state.messages.Edit;
import io.getstream.chat.android.ui.common.state.messages.MessageMode;
import io.getstream.chat.android.ui.feature.messages.header.MessageListHeaderView;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager;
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory;
import kotlinx.coroutines.flow.Flow;

public class ChannelActivity3 extends AppCompatActivity {

    private final static String CID_KEY = "key:cid";

    public static Intent newIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelActivity.class);
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
        ViewModelProvider.Factory factory = new MessageListViewModelFactory.Builder(this)
                .cid(cid)
                .build();
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageComposerViewModel messageComposerViewModel = provider.get(MessageComposerViewModel.class);

        // Set a view factory manager for Imgur attachments
        ImgurAttachmentFactory imgurAttachmentFactory = new ImgurAttachmentFactory();

        List<ImgurAttachmentFactory> imgurAttachmentViewFactories = new ArrayList<ImgurAttachmentFactory>();
        imgurAttachmentViewFactories.add(imgurAttachmentFactory);

        AttachmentFactoryManager attachmentFactoryManager = new AttachmentFactoryManager(imgurAttachmentViewFactories);
        binding.messageListView.setAttachmentFactoryManager(attachmentFactoryManager);

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this);
        MessageComposerViewModelBinding.bind(messageComposerViewModel, binding.messageComposerView, this);

        // Step 3 - Let both MessageListHeaderView and MessageComposerView know when we open a thread
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof MessageMode.MessageThread) {
                Message parentMessage = ((MessageMode.MessageThread) mode).getParentMessage();
                messageListHeaderViewModel.setActiveThread(parentMessage);
                messageComposerViewModel.setMessageMode(new MessageMode.MessageThread(parentMessage));
            } else if (mode instanceof MessageMode.Normal) {
                messageListHeaderViewModel.resetThread();
                messageComposerViewModel.leaveThread();
            }
        });

        // Step 4 - Let the message input know when we are editing a message
        binding.messageListView.setMessageEditHandler(message -> {
            messageComposerViewModel.performMessageAction(new Edit(message));
        });

        // Step 5 - Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof MessageListViewModel.State.NavigateUp) {
                finish();
            }
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessageListHeaderView.OnClickListener backHandler = () -> messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
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
                FlowExtensions.asLiveData(channelStateFlow),
                channelState -> FlowExtensions.asLiveData(channelState.getTyping())
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
