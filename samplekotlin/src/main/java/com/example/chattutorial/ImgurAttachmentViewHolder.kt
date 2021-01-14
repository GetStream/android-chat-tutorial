package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.example.chattutorial.databinding.ListItemAttachImgurBinding
//import com.getstream.sdk.chat.adapter.AttachmentListItem
import com.getstream.sdk.chat.adapter.MessageListItem
//import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder
//import com.getstream.sdk.chat.view.MessageListView
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemPayloadDiff
import io.getstream.chat.android.ui.messages.view.MessageListView

class ImgurAttachmentViewHolder(
    parent: ViewGroup,
    private val binding: ListItemAttachImgurBinding = ListItemAttachImgurBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

//    override fun bind(attachmentListItem: AttachmentListItem) {
//        val background = bubbleHelper.getDrawableForAttachment(
//            messageItem.message,
//            messageItem.isMine,
//            messageItem.positions,
//            attachmentListItem.attachment
//        )
//        binding.ivMediaThumb.setShape(context, background)
//
//        binding.ivMediaThumb.load(attachmentListItem.attachment.thumbUrl) {
//            allowHardware(false)
//        }
//    }

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        data.message.attachments.firstOrNull()?.imageUrl?.let {
            binding.ivMediaThumb.load(it) {
                allowHardware(false)
            }
        }
    }
}
