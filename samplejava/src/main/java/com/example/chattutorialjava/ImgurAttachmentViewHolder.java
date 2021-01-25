package com.example.chattutorialjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.chattutorialjava.databinding.ViewHolderImgurAttachmentBinding;
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

    ViewHolderImgurAttachmentBinding binding;

    public static ImgurAttachmentViewHolder create(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderImgurAttachmentBinding binding = ViewHolderImgurAttachmentBinding.inflate(inflater, parent, false);
        return new ImgurAttachmentViewHolder(binding);
    }

    private ImgurAttachmentViewHolder(@NotNull ViewHolderImgurAttachmentBinding binding) {
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
                .target(binding.ivMediaThumb)
                .build();

        Coil.imageLoader(context).enqueue(imageRequest);

        align(data);
    }

    private void align(MessageListItem.MessageItem data) {
        ConstraintSet set = new ConstraintSet();
        ConstraintLayout root = binding.getRoot();
        set.clone(root);
        Integer pinnedPosition = getPinnedPosition(data.isMine());
        Integer clearedPosition = getPinnedPosition(!data.isMine());
        int imageViewId = binding.ivMediaThumb.getId();
        set.clear(imageViewId, clearedPosition);
        set.connect(
                imageViewId,
                pinnedPosition,
                ConstraintSet.PARENT_ID,
                pinnedPosition,
                (int) root.getResources().getDimension(R.dimen.stream_ui_spacing_small)
        );
        set.applyTo(root);
    }

    private Integer getPinnedPosition(Boolean isMine) {
        return isMine ? ConstraintSet.END : ConstraintSet.START;
    }
}
