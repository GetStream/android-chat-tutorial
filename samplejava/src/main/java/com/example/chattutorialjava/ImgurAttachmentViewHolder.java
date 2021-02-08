package com.example.chattutorialjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chattutorialjava.databinding.ListItemAttachmentImgurBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.messages.adapter.MessageListItemPayloadDiff;

class ImgurAttachmentViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {

    ListItemAttachmentImgurBinding binding;

    public static ImgurAttachmentViewHolder create(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemAttachmentImgurBinding binding = ListItemAttachmentImgurBinding.inflate(inflater, parent, false);
        return new ImgurAttachmentViewHolder(binding);
    }

    private ImgurAttachmentViewHolder(@NotNull ListItemAttachmentImgurBinding binding) {
        super(binding.getRoot());

        float cornerRadius = binding.getRoot()
                .getResources()
                .getDimension(R.dimen.stream_ui_selected_attachment_corner_radius);

        ShapeAppearanceModel model = binding.ivMediaThumb.getShapeAppearanceModel()
                .toBuilder()
                .setAllCornerSizes(cornerRadius)
                .build();

        binding.ivMediaThumb.setShapeAppearanceModel(model);
        binding.ivMediaThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

        this.binding = binding;
    }

    @Override
    public void bindData(
            @NotNull MessageListItem.MessageItem data,
            @Nullable MessageListItemPayloadDiff diff
    ) {
        List<Attachment> attachments = data.getMessage().getAttachments();
        String imageUrl = attachments.get(0).getImageUrl();
        Context context = getContext();

        ImageRequest imageRequest = new ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .crossfade(true)
                .placeholder(R.drawable.stream_ui_picture_placeholder)
                .target(binding.ivMediaThumb)
                .build();

        Coil.imageLoader(context).enqueue(imageRequest);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.ivMediaThumb.getLayoutParams();
        params.horizontalBias = data.isMine() ? 1f : 0f;
        binding.ivMediaThumb.setLayoutParams(params);
    }
}
