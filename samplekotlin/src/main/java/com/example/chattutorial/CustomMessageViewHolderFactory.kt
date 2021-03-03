package com.example.chattutorial

import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory

class CustomMessageViewHolderFactory : MessageListItemViewHolderFactory() {

    override fun createViewHolder(
            parentView: ViewGroup,
            viewType: Int
    ): BaseMessageItemViewHolder<out MessageListItem> {
        return CustomMessageViewHolder(parentView)
    }

}
