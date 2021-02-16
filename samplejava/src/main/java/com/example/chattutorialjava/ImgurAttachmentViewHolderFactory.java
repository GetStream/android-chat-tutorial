package com.example.chattutorialjava;

import android.view.ViewGroup;

import com.getstream.sdk.chat.adapter.MessageListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory;

class ImgurAttachmentViewHolderFactory extends MessageListItemViewHolderFactory {

    private static final Integer IMGUR = 0;

    @Override
    public int getItemViewType(@NotNull MessageListItem item) {
        return hasImgurImage(item) ? IMGUR : super.getItemViewType(item);
    }

    @NotNull
    @Override
    public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(
            @NotNull ViewGroup parentView,
            int viewType
    ) {
        boolean isImgur = viewType == IMGUR;
        return isImgur ? ImgurAttachmentViewHolder.create(parentView) : super.createViewHolder(parentView, viewType);
    }

    private boolean hasImgurImage(MessageListItem item) {
        if (!(item instanceof MessageListItem.MessageItem)) {
            return false;
        }

        Message message = ((MessageListItem.MessageItem) item).getMessage();
        List<Attachment> attachments = message.getAttachments();
        if (attachments.isEmpty()) {
            return false;
        }

        Attachment attachment = attachments.get(0);
        if (attachment == null) {
            return false;
        }

        String imageUrl = attachment.getImageUrl();
        if (imageUrl == null) {
            return false;
        }

        return imageUrl.contains("imgur");
    }
}
