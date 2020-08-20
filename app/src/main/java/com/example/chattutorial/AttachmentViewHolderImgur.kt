package com.example.chattutorial


import android.content.Context
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.getstream.sdk.chat.adapter.BaseAttachmentViewHolder
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.utils.roundedImageView.PorterShapeImageView
import com.getstream.sdk.chat.view.MessageListView
import com.getstream.sdk.chat.view.MessageListViewStyle
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message

class AttachmentViewHolderImgur(resId: Int, parent: ViewGroup) :
    BaseAttachmentViewHolder(resId, parent) {
    private val ivMediaThumb: PorterShapeImageView = itemView.findViewById(R.id.iv_media_thumb)

    override fun bind(
        context: Context,
        messageListItem: MessageListItem.MessageItem,
        message: Message,
        attachment: Attachment,
        style: MessageListViewStyle,
        bubbleHelper: MessageListView.BubbleHelper,
        clickListener: MessageListView.AttachmentClickListener?,
        longClickListener: MessageListView.MessageLongClickListener?
    ) {
        val background = bubbleHelper.getDrawableForAttachment(
            messageListItem.message,
            messageListItem.isMine,
            messageListItem.positions,
            attachment
        )
        ivMediaThumb.setShape(context, background)

        Glide.with(context)
            .load(attachment.thumbUrl)
            .into(ivMediaThumb)
    }
}