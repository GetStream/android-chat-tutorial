package com.example.chattutorial;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chattutorial.databinding.AttachmentImgurBinding;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.jetbrains.annotations.NotNull;

import coil.Coil;
import coil.request.ImageRequest;
import io.getstream.chat.android.models.Attachment;
import io.getstream.chat.android.models.Message;
import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListListenerContainer;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactory;
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.InnerAttachmentViewHolder;

/** A custom attachment factory to show an imgur logo if the attachment URL is an imgur image. **/
public class ImgurAttachmentFactory implements AttachmentFactory {


    // Step 1 - Check whether the message contains an Imgur attachment
    @Override
    public boolean canHandle(@NonNull Message message) {
        return containsImgurAttachments(message) != null;
    }

    // Step 2 - Create the ViewHolder that will be used to display the Imgur logo
    // over Imgur attachments
    @NonNull
    @Override
    public InnerAttachmentViewHolder createViewHolder(
            @NonNull Message message,
            @Nullable MessageListListenerContainer listeners,
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
                ImageRequest imageRequest = new ImageRequest.Builder(binding.getRoot().getContext())
                        .data(imgurAttachment.getImageUrl())
                        .allowHardware(false)
                        .crossfade(true)
                        .placeholder(io.getstream.chat.android.ui.R.drawable.stream_ui_picture_placeholder)
                        .target(binding.ivMediaThumb)
                        .build();
                Coil.imageLoader(binding.getRoot().getContext()).enqueue(imageRequest);
            }
        }
    }
}
