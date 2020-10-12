package com.example.chattutorialjava;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.getstream.sdk.chat.adapter.AttachmentListItem;
import com.getstream.sdk.chat.adapter.MessageListItem.MessageItem;
import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder;
import com.getstream.sdk.chat.utils.roundedImageView.PorterShapeImageView;
import com.getstream.sdk.chat.view.MessageListView.BubbleHelper;

import org.jetbrains.annotations.NotNull;

public class AttachmentViewHolderImgur extends BaseAttachmentViewHolder {
    private final BubbleHelper bubbleHelper;
    private final MessageItem messageItem;
    private final PorterShapeImageView imageView;

    public AttachmentViewHolderImgur(
            ViewGroup parent,
            BubbleHelper bubbleHelper,
            MessageItem messageItem
    ) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attach_imgur, parent, false));
        this.bubbleHelper = bubbleHelper;
        this.messageItem = messageItem;
        imageView = itemView.findViewById(R.id.iv_media_thumb);
    }

    @Override
    public void bind(@NotNull AttachmentListItem attachmentListItem) {
        Drawable background = bubbleHelper.getDrawableForAttachment(
                messageItem.getMessage(),
                messageItem.isMine(),
                messageItem.getPositions(),
                attachmentListItem.getAttachment());
        imageView.setShape(getContext(), background);

        Glide.with(getContext())
                .load(attachmentListItem.getAttachment().getThumbUrl())
                .into(imageView);
    }
}
