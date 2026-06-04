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

import com.example.chattutorial.databinding.ActivityChannel4Binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.events.TypingStartEvent;
import io.getstream.chat.android.client.events.TypingStopEvent;
import io.getstream.chat.android.models.Channel;
import io.getstream.chat.android.models.Message;
import io.getstream.chat.android.models.User;
import io.getstream.chat.android.ui.common.state.messages.Edit;
import io.getstream.chat.android.ui.common.state.messages.MessageMode;
import io.getstream.chat.android.ui.feature.messages.header.ChannelHeaderView;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager;
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.ChannelHeaderViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.ChannelHeaderViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel;
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.messages.ChannelViewModelFactory;

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

        // inflate binding
        ActivityChannel4Binding binding = ActivityChannel4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.applySystemBarInsetsAsPadding(binding.getRoot());

        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity4");
        }

        // Create three separate ViewModels for the views so it's easy
        // to customize them individually
        ViewModelProvider.Factory factory = new ChannelViewModelFactory.Builder(this)
                .cid(cid)
                .build();
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        ChannelHeaderViewModel channelHeaderViewModel = provider.get(ChannelHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageComposerViewModel messageComposerViewModel = provider.get(MessageComposerViewModel.class);

        // Set a view factory manager for Imgur attachments
        ImgurAttachmentFactory imgurAttachmentFactory = new ImgurAttachmentFactory();

        List<ImgurAttachmentFactory> imgurAttachmentViewFactories = new ArrayList<>();
        imgurAttachmentViewFactories.add(imgurAttachmentFactory);

        AttachmentFactoryManager attachmentFactoryManager = new AttachmentFactoryManager(imgurAttachmentViewFactories);
        binding.messageListView.setAttachmentFactoryManager(attachmentFactoryManager);

        // Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        ChannelHeaderViewModelBinding.bind(channelHeaderViewModel, binding.channelHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this);
        MessageComposerViewModelBinding.bind(messageComposerViewModel, binding.messageComposerView, this);

        // Let both ChannelHeaderView and MessageComposerView know when we open a thread
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof MessageMode.MessageThread) {
                Message parentMessage = ((MessageMode.MessageThread) mode).getParentMessage();
                channelHeaderViewModel.setActiveThread(parentMessage);
                messageComposerViewModel.setMessageMode(new MessageMode.MessageThread(parentMessage));
            } else if (mode instanceof MessageMode.Normal) {
                channelHeaderViewModel.resetThread();
                messageComposerViewModel.leaveThread();
            }
        });

        // Let the message input know when we are editing a message
        binding.messageListView.setMessageEditHandler(message -> {
            messageComposerViewModel.performMessageAction(new Edit(message));
        });

        // Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof MessageListViewModel.State.NavigateUp) {
                finish();
            }
        });

        // Handle back button behaviour correctly when you're in a thread
        ChannelHeaderView.OnClickListener backHandler = () -> messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
        binding.channelHeaderView.setBackButtonClickListener(backHandler);
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

        // Observe raw events through the low-level client
        Set<String> currentlyTyping = new HashSet<>();
        ChatClient.instance()
                .channel(cid)
                .subscribeFor(
                        this,
                        new Class[]{TypingStartEvent.class, TypingStopEvent.class},
                        event -> {
                            if (event instanceof TypingStartEvent) {
                                User user = ((TypingStartEvent) event).getUser();
                                currentlyTyping.add(user.getName());
                            } else if (event instanceof TypingStopEvent) {
                                User user = ((TypingStopEvent) event).getUser();
                                currentlyTyping.remove(user.getName());
                            }

                            String typing = "nobody is typing";
                            if (!currentlyTyping.isEmpty()) {
                                typing = "typing: " + TextUtils.join(", ", currentlyTyping);
                            }

                            typingHeaderView.setText(typing);
                        }
                );
    }
}
