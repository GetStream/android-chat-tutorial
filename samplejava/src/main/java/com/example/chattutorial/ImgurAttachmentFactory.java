package com.example.chattutorial;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chattutorial.databinding.AttachmentImgurBinding;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.jetbrains.annotations.NotNull;

import coil3.SingletonImageLoader;
import coil3.request.ImageRequest;
import coil3.request.ImageRequestsKt;
import coil3.request.ImageRequests_androidKt;
import io.getstream.chat.android.models.Attachment;
import io.getstream.chat.android.models.Message;
import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListListeners;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.BaseAttachmentFactory;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.InnerAttachmentViewHolder;

/**
 * A custom attachment factory to show an imgur logo if the attachment URL is an imgur image.
 **/
public class ImgurAttachmentFactory extends BaseAttachmentFactory {


    // Check whether the message contains an Imgur attachment
    @Override
    public boolean canHandle(@NonNull Message message) {
        return containsImgurAttachments(message) != null;
    }

    // Create the ViewHolder that will be used to display the Imgur logo
    // over Imgur attachments
    @NonNull
    @Override
    public InnerAttachmentViewHolder createViewHolder(
            @NonNull Message message,
            @Nullable MessageListListeners listeners,
            @NonNull ViewGroup parent
    ) {
        Attachment imgurAttachment = containsImgurAttachments(message);

        AttachmentImgurBinding attachmentImgurBinding = AttachmentImgurBinding.inflate(LayoutInflater.from(parent.getContext()), null, false);

        return new ImgurAttachmentViewHolder(attachmentImgurBinding, imgurAttachment);
    }

    private Attachment containsImgurAttachments(@NotNull Message message) {
        for (int i = 0; i < message.getAttachments().size(); i++) {
            String imageUrl = message.getAttachments().get(i).getImageUrl();

            if (imageUrl != null && imageUrl.contains("imgur")) {
                return message.getAttachments().get(i);
            }
        }

        return null;
    }

    private static class ImgurAttachmentViewHolder extends InnerAttachmentViewHolder {

        public ImgurAttachmentViewHolder(AttachmentImgurBinding binding,
                                         @Nullable Attachment imgurAttachment) {
            super(binding.getRoot());

            ShapeAppearanceModel shapeAppearanceModel = binding.ivMediaThumb.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCornerSizes(binding.ivMediaThumb.getResources().getDimension(io.getstream.chat.android.ui.R.dimen.stream_ui_selected_attachment_corner_radius))
                    .build();

            binding.ivMediaThumb.setShapeAppearanceModel(shapeAppearanceModel);

            if (imgurAttachment != null) {
                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(binding.getRoot().getContext())
                        .data(imgurAttachment.getImageUrl());
                ImageRequests_androidKt.allowHardware(requestBuilder, false);
                ImageRequestsKt.crossfade(requestBuilder, true);
                ImageRequests_androidKt.placeholder(requestBuilder, io.getstream.chat.android.ui.R.drawable.stream_ui_picture_placeholder);
                ImageRequests_androidKt.target(requestBuilder, binding.ivMediaThumb);
                SingletonImageLoader.get(binding.getRoot().getContext()).enqueue(requestBuilder.build());
            }
        }
    }
}
