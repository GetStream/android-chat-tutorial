package com.example.chattutorial

import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.AttachmentListItemAdapter
import com.getstream.sdk.chat.adapter.BaseAttachmentViewHolder
import com.getstream.sdk.chat.adapter.MessageViewHolderFactory
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message

class MyMessageViewHolderFactory : MessageViewHolderFactory() {

    override fun getAttachmentViewType(
        message: Message?,
        mine: Boolean?,
        position: Position?,
        attachments: List<Attachment>?,
        attachment: Attachment
    ): Int {
        val imageUrl = attachment.imageUrl ?: ""
        return if (imageUrl.indexOf("imgur") != -1) {
            IMGUR_TYPE
        } else {
            super.getAttachmentViewType(
                message,
                mine,
                position,
                attachments,
                attachment
            )
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
