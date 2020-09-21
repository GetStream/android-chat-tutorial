package com.example.chattutorial

import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.AttachmentListItem
import com.getstream.sdk.chat.adapter.AttachmentListItemAdapter
import com.getstream.sdk.chat.adapter.BaseAttachmentViewHolder
import com.getstream.sdk.chat.adapter.MessageViewHolderFactory
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message

class MyMessageViewHolderFactory : MessageViewHolderFactory() {

    override fun getAttachmentViewType(
        attachmentItem: AttachmentListItem
    ): Int {
        val imageUrl = attachmentItem.attachment.imageUrl ?: ""
        return if (imageUrl.indexOf("imgur") != -1) {
            IMGUR_TYPE
        } else {
            super.getAttachmentViewType(attachmentItem)
        }
    }

    override fun createAttachmentViewHolder(
        adapter: AttachmentListItemAdapter,
        parent: ViewGroup,
        viewType: Int
    ): BaseAttachmentViewHolder = if (viewType == IMGUR_TYPE) {
        AttachmentViewHolderImgur(R.layout.list_item_attach_imgur, parent)
    } else {
        super.createAttachmentViewHolder(adapter, parent, viewType)
    }

    companion object {
        private const val IMGUR_TYPE = 0
    }
}
