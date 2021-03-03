package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chattutorial.databinding.ListItemCustomMessageBinding
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff

class CustomMessageViewHolder(
    parent: ViewGroup,
    private val binding: ListItemCustomMessageBinding = ListItemCustomMessageBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        binding.avatar.setUserData(data.message.user)
        binding.message.text = data.message.text
        binding.name.text = data.message.user.name
    }
}
