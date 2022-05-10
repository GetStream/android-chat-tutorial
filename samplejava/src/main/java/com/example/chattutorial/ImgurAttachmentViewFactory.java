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
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer;
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.AttachmentFactory;
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.InnerAttachmentViewHolder;

public class ImgurAttachmentViewFactory implements AttachmentFactory {


    @Override
    public boolean canHandle(@NonNull Message message) {
        return containsImgurAttachments(message) != null;
    }

    @NonNull
    @Override
    public InnerAttachmentViewHolder createViewHolder(@NonNull Message message, @Nullable MessageListListenerContainer messageListListenerContainer, @NonNull ViewGroup viewGroup) {
        Attachment imgurAttachment = containsImgurAttachments(message);

        AttachmentImgurBinding attachmentImgurBinding = AttachmentImgurBinding.inflate(LayoutInflater.from(viewGroup.getContext()), null, false);

        return new ImgurAttachmentViewHolder(attachmentImgurBinding, imgurAttachment);
    }

    private Attachment containsImgurAttachments(@NotNull Message message) {
        for (int i = 0; i < message.getAttachments().size(); i++) {
            boolean containsAttachments = message.getAttachments().get(i).getImageUrl().contains("imgur");

            if (containsAttachments) {
                return message.getAttachments().get(i);
            }
        }

        return null;
    }

    private class ImgurAttachmentViewHolder extends InnerAttachmentViewHolder {

        public ImgurAttachmentViewHolder(AttachmentImgurBinding binding,
                                         Attachment imgurAttachment) {
            super(binding.getRoot());

            ShapeAppearanceModel shapeAppearanceModel = binding.ivMediaThumb.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCornerSizes(binding.ivMediaThumb.getResources().getDimension(R.dimen.stream_ui_selected_attachment_corner_radius))
                    .build();

            binding.ivMediaThumb.setShapeAppearanceModel(shapeAppearanceModel);

            ImageRequest imageRequest = new ImageRequest.Builder(binding.getRoot().getContext())
                    .data(imgurAttachment.getImageUrl())
                    .allowHardware(false)
                    .crossfade(true)
                    .placeholder(R.drawable.stream_ui_picture_placeholder)
                    .target(binding.ivMediaThumb)
                    .build();
            Coil.imageLoader(binding.getRoot().getContext()).enqueue(imageRequest);
        }
    }
}
