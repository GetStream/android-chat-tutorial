package com.example.chattutorialjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.getstream.sdk.chat.view.MessageListView;
import com.getstream.sdk.chat.view.messageinput.MessageInputView;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModelBinding;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModelBinding;

import java.util.LinkedList;
import java.util.List;

import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.livedata.controller.ChannelController;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class ChannelActivity3 extends AppCompatActivity {

    private final static String CID_KEY = "key:cid";
    private final static int MESSAGE_LIMIT = 30;

    public static Intent newIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelActivity3.class);
        intent.putExtra(CID_KEY, channel.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_3);
        final MessageListView messageListView = findViewById(R.id.messageListView);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView channelHeaderView = findViewById(R.id.channelHeaderView);
        final MessageInputView messageInputView = findViewById(R.id.messageInputView);

        messageListView.setAttachmentViewHolderFactory(new MyAttachmentViewHolderFactory());

        final String cid = getIntent().getStringExtra(CID_KEY);
        final ViewModelProvider viewModelProvider = new ViewModelProvider(this, new ChannelViewModelsFactory(cid));

        final MessageListViewModel messageListViewModel = viewModelProvider.get(MessageListViewModel.class);
        MessageListViewModelBinding.bind(messageListViewModel, messageListView, this);
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof MessageListViewModel.State.Loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof MessageListViewModel.State.Result) {
                progressBar.setVisibility(View.GONE);
            } else if (state instanceof MessageListViewModel.State.NavigateUp) {
                finish();
            }
        });

<<<<<<< HEAD
        String nobodyTypingText = "nobody is typing";
        channelHeaderView.setText(nobodyTypingText);
        ChatDomain.instance().getUseCases().getWatchChannel().invoke(cid, MESSAGE_LIMIT).enqueue(result -> {
            if (result.isSuccess()) {
                ChannelController channelController = result.data();
                runOnUiThread(() -> channelController.getTyping().observe(this, users -> {
                    String typing = nobodyTypingText;
                    if (!users.isEmpty()) {
                        List<String> names = new LinkedList<>();
                        for (User user : users) {
                            names.add((String) user.getExtraData().get("name"));
                        }
                        typing = "typing: " + TextUtils.join(", ", names);
                    }
                    channelHeaderView.setText(typing);
                }));
            }
            return Unit.INSTANCE;
=======
        ChannelController channelController = ChatClient.instance().channel(cid);
        MutableLiveData<Set<String>> currentlyTyping = new MutableLiveData<>(new HashSet<>());
        channelController.subscribeFor(
                new Class[]{TypingStartEvent.class, TypingStartEvent.class},
                event -> {
                    if (event instanceof TypingStartEvent) {
                        User user = ((TypingStartEvent) event).getUser();
                        String name = (String) user.getExtraData().get("name");
                        Set<String> typingCopy = currentlyTyping.getValue();
                        typingCopy.add(name);
                        currentlyTyping.postValue(typingCopy);
                    } else if (event instanceof TypingStopEvent) {
                        User user = ((TypingStopEvent) event).getUser();
                        String name = (String) user.getExtraData().get("name");
                        Set<String> typingCopy = currentlyTyping.getValue();
                        typingCopy.remove(name);
                        currentlyTyping.postValue(typingCopy);
                    }
                    return null;
                });
        currentlyTyping.observe(this, users -> {
            String typing = "nobody is typing";
            if (!users.isEmpty()) {
                typing = "typing: " + TextUtils.join(", ", users);
            }
            channelHeaderView.setText(typing);
>>>>>>> 23067e524310df9e383d921476e1f7625c2b04df
        });

        final MessageInputViewModel messageInputViewModel = viewModelProvider.get(MessageInputViewModel.class);
        MessageInputViewModelBinding.bind(messageInputViewModel, messageInputView, this);
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof MessageListViewModel.Mode.Thread) {
                messageInputViewModel.setActiveThread(((MessageListViewModel.Mode.Thread) mode).getParentMessage());
            } else if (mode instanceof MessageListViewModel.Mode.Normal) {
                messageInputViewModel.resetThread();
            }
            messageListView.setOnMessageEditHandler((message) -> {
                messageInputViewModel.getEditMessage().postValue(message);
                return Unit.INSTANCE;
            });
        });

        Function0<Unit> onBackClick = () -> {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
            return Unit.INSTANCE;
        };

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackClick.invoke();
            }
        });
    }
}
