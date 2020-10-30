package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.example.chattutorial.databinding.ListItemAttachImgurBinding
import com.getstream.sdk.chat.adapter.AttachmentListItem
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder
import com.getstream.sdk.chat.view.MessageListView

class AttachmentViewHolderImgur(
    parent: ViewGroup,
    private val bubbleHelper: MessageListView.BubbleHelper,
    private val messageItem: MessageListItem.MessageItem,
    private val binding: ListItemAttachImgurBinding = ListItemAttachImgurBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : BaseAttachmentViewHolder(binding.root) {

    override fun bind(attachmentListItem: AttachmentListItem) {
        val background = bubbleHelper.getDrawableForAttachment(
            messageItem.message,
            messageItem.isMine,
            messageItem.positions,
            attachmentListItem.attachment
        )
        binding.ivMediaThumb.setShape(context, background)

        binding.ivMediaThumb.load(attachmentListItem.attachment.thumbUrl) {
            allowHardware(false)
        }
    }
}
