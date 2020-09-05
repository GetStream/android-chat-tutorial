package com.example.chattutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel

class ChannelViewModelsFactory(private val cid: String) : NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MessageListViewModel::class.java -> {
                MessageListViewModel(cid) as T
            }
            ChannelHeaderViewModel::class.java -> {
                ChannelHeaderViewModel(cid) as T
            }
            MessageInputViewModel::class.java -> {
                MessageInputViewModel(cid) as T
            }
            else -> {
                super.create(modelClass)
            }
        }
    }
}
