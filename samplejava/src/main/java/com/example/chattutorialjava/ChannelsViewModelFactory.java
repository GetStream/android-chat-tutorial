package com.example.chattutorialjava;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.getstream.sdk.chat.viewmodel.channels.ChannelsViewModelImpl;

import io.getstream.chat.android.client.api.models.QuerySort;
import io.getstream.chat.android.client.utils.FilterObject;
import io.getstream.chat.android.livedata.ChatDomain;

// TODO: move to library?
public class ChannelsViewModelFactory implements ViewModelProvider.Factory {

    private final FilterObject filter;
    private final QuerySort sort;

    public ChannelsViewModelFactory(FilterObject filter, QuerySort sort) {
        this.filter = filter;
        this.sort = sort;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.equals(ChannelsViewModelImpl.class)) {
            throw new IllegalArgumentException("This factory can only produce ChannelsViewModelImpl instances");
        }
        return (T) new ChannelsViewModelImpl(
                ChatDomain.instance(),
                filter,
                sort
        );
    }
}
