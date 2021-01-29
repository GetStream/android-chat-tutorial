package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import coil.load
import com.example.chattutorial.databinding.ListItemAttachmentImgurBinding
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemPayloadDiff

class ImgurAttachmentViewHolder(
    parent: ViewGroup,
    private val binding: ListItemAttachmentImgurBinding = ListItemAttachmentImgurBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

    init {
        binding.ivMediaThumb.apply {
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(resources.getDimension(R.dimen.stream_ui_selected_attachment_corner_radius))
                .build()
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        data.message.attachments.firstOrNull()?.imageUrl?.let { url ->
            binding.ivMediaThumb.apply {
                load(url) {
                    allowHardware(false)
                    crossfade(true)
                    placeholder(R.drawable.stream_ui_picture_placeholder)
                }

                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    horizontalBias = if (data.isMine) 1f else 0f
                }
            }
        }
    }
}
