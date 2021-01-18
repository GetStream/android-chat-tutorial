package com.example.chattutorialjava;

import android.view.ViewGroup;

import com.getstream.sdk.chat.adapter.MessageListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.messages.adapter.MessageListItemViewHolderFactory;

class ImgurAttachmentViewHolderFactory extends MessageListItemViewHolderFactory {

    private static final Integer IMGUR = 999;

    @Override
    public int getItemViewType(@NotNull MessageListItem item) {

        boolean isMessageItem = item instanceof MessageListItem.MessageItem;
        if (!isMessageItem) {
            return super.getItemViewType(item);
        }

        Message message = ((MessageListItem.MessageItem) item).getMessage();
        List<Attachment> attachments = message.getAttachments();

        if (attachments == null || attachments.isEmpty()) {
            return super.getItemViewType(item);
        }

        Attachment attachment = attachments.get(0);
        if (attachment == null) {
            return super.getItemViewType(item);
        }

        String imageUrl = attachment.getImageUrl();
        if (imageUrl == null) {
            return super.getItemViewType(item);
        }

        boolean isImgur = imageUrl.contains("imgur");
        if (isImgur) {
            return IMGUR;
        }

        return super.getItemViewType(item);
    }

    @NotNull
    @Override
    public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(@NotNull ViewGroup parentView, int viewType) {
        boolean isImgur = viewType == IMGUR;
        return isImgur ? ImgurAttachmentViewHolder.create(parentView) : super.createViewHolder(parentView, viewType);
    }
}
