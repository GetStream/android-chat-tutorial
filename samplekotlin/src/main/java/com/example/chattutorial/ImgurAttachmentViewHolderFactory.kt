package com.example.chattutorial

import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemViewHolderFactory

class ImgurAttachmentViewHolderFactory : MessageListItemViewHolderFactory() {

    override fun getItemViewType(item: MessageListItem): Int {
        val isImgur = when (item) {
            is MessageListItem.MessageItem -> {
                item.message
                    .attachments
                    .firstOrNull()
                    ?.imageUrl
                    ?.contains("imgur") == true
            }

            else -> false
        }

        return if (isImgur) IMGUR else super.getItemViewType(item)
    }

    override fun createViewHolder(
        parentView: ViewGroup,
        viewType: Int
    ): BaseMessageItemViewHolder<out MessageListItem> {
        return when (viewType) {
            IMGUR -> ImgurAttachmentViewHolder(parentView)
            else -> super.createViewHolder(parentView, viewType)
        }
    }

    companion object {
        private const val IMGUR = 999
    }
}
