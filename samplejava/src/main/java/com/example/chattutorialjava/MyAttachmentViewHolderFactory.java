package com.example.chattutorialjava;

import android.view.ViewGroup;

import com.getstream.sdk.chat.adapter.AttachmentListItem;
import com.getstream.sdk.chat.adapter.AttachmentViewHolderFactory;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder;
import com.getstream.sdk.chat.view.MessageListViewStyle;

import org.jetbrains.annotations.NotNull;

public class MyAttachmentViewHolderFactory extends AttachmentViewHolderFactory {
    private static final int IMGUR_TYPE = 0;

    @Override
    public int getAttachmentViewType(@NotNull AttachmentListItem attachmentListItem) {
        String imageUrl = attachmentListItem.getAttachment().getImageUrl();
        if (imageUrl != null && imageUrl.contains("imgur")) {
            return IMGUR_TYPE;
        }
        return super.getAttachmentViewType(attachmentListItem);
    }

    @NotNull
    @Override
    public BaseAttachmentViewHolder createAttachmentViewHolder(
            @NotNull ViewGroup parent,
            int viewType,
            @NotNull MessageListViewStyle style,
            @NotNull MessageListItem.MessageItem messageItem) {

        if (viewType == IMGUR_TYPE) {
            return new AttachmentViewHolderImgur(parent, bubbleHelper, messageItem);
        }
        return super.createAttachmentViewHolder(parent, viewType, style, messageItem);
    }
}

