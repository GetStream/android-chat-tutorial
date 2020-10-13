package com.example.chattutorialjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.getstream.sdk.chat.view.ChannelHeaderView;
import com.getstream.sdk.chat.view.messageinput.MessageInputView;
import com.getstream.sdk.chat.view.MessageListView;
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel;
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModelBinding;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModelBinding;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModelBinding;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import io.getstream.chat.android.client.models.Channel;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class ChannelActivity2 extends AppCompatActivity {

    private final static String CID_KEY = "key:cid";

    public static Intent newIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelActivity2.class);
        intent.putExtra(CID_KEY, channel.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_2);
        final MessageListView messageListView = findViewById(R.id.messageListView);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final ChannelHeaderView channelHeaderView = findViewById(R.id.channelHeaderView);
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

        ChannelHeaderViewModelBinding.bind(viewModelProvider.get(ChannelHeaderViewModel.class), channelHeaderView, this);

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
        channelHeaderView.setOnBackClick(onBackClick);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackClick.invoke();
            }
        });
    }
}
