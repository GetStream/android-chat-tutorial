package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.getstream.sdk.chat.adapter.AttachmentListItem
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder
import com.getstream.sdk.chat.utils.roundedImageView.PorterShapeImageView
import com.getstream.sdk.chat.view.MessageListView
import kotlinx.android.synthetic.main.list_item_attach_imgur.view.*

class AttachmentViewHolderImgur(
    parent: ViewGroup,
    private val bubbleHelper: MessageListView.BubbleHelper,
    private val messageItem: MessageListItem.MessageItem
) : BaseAttachmentViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.list_item_attach_imgur, parent, false)
) {

    private val ivMediaThumb: PorterShapeImageView = itemView.iv_media_thumb

    override fun bind(attachmentListItem: AttachmentListItem) {
        val background = bubbleHelper.getDrawableForAttachment(
            messageItem.message,
            messageItem.isMine,
            messageItem.positions,
            attachmentListItem.attachment
        )
        ivMediaThumb.setShape(context, background)

        Glide.with(context)
            .load(attachmentListItem.attachment.thumbUrl)
            .into(ivMediaThumb)
    }
}
