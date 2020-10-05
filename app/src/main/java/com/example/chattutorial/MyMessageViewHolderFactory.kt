package com.example.chattutorial

import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.*
import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder
import com.getstream.sdk.chat.view.MessageListViewStyle

class MyAttachmentViewHolderFactory : AttachmentViewHolderFactory() {

    override fun getAttachmentViewType(attachmentItem: AttachmentListItem): Int {
        val imageUrl = attachmentItem.attachment.imageUrl ?: ""
        return when {
            imageUrl.indexOf("imgur") != -1 -> IMGUR_TYPE
            else -> super.getAttachmentViewType(attachmentItem)
        }
    }

    override fun createAttachmentViewHolder(
        parent: ViewGroup,
        viewType: Int,
        style: MessageListViewStyle,
        messageItem: MessageListItem.MessageItem
    ): BaseAttachmentViewHolder {
        return when (viewType) {
            IMGUR_TYPE -> AttachmentViewHolderImgur(parent, bubbleHelper, messageItem)
            else -> super.createAttachmentViewHolder(parent, viewType, style, messageItem)
        }
    }

    companion object {
        private const val IMGUR_TYPE = 0
    }
}
