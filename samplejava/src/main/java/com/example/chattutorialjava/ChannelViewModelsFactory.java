package com.example.chattutorialjava;

import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@SuppressWarnings("unchecked")
public class ChannelViewModelsFactory extends ViewModelProvider.NewInstanceFactory {

    private final String cid;

    public ChannelViewModelsFactory(@NonNull String cid) {
        this.cid = cid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MessageListViewModel.class) {
            return (T) new MessageListViewModel(cid);
        } else if (modelClass == ChannelHeaderViewModel.class) {
            return (T) new ChannelHeaderViewModel(cid);
        } else if (modelClass == MessageInputViewModel.class) {
            return (T) new MessageInputViewModel(cid);
        } else {
            return super.create(modelClass);
        }
    }
}
