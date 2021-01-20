package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import coil.load
import com.example.chattutorial.databinding.ViewHolderImgurAttachmentBinding
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemPayloadDiff

class ImgurAttachmentViewHolder(
    parent: ViewGroup,
    private val binding: ViewHolderImgurAttachmentBinding = ViewHolderImgurAttachmentBinding.inflate(
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

    override fun bindData(data: MessageListItem.MessageItem, isThread: Boolean, diff: MessageListItemPayloadDiff?) {
        data.message.attachments.firstOrNull()?.imageUrl?.let { url ->
            binding.ivMediaThumb.apply {
                load(url) {
                    allowHardware(false)
                    crossfade(true)
                    placeholder(R.drawable.stream_ui_picture_placeholder)
                }
            }

            align(data)
        }
    }

    private fun align(data: MessageListItem.MessageItem) {
        ConstraintSet().apply {
            with(binding) {
                clone(root)
                val pinnedPosition = getPinnedPosition(data.isMine)
                val clearedPosition = getPinnedPosition(!data.isMine)
                val imageViewId = ivMediaThumb.id
                clear(imageViewId, clearedPosition)
                connect(
                    imageViewId,
                    pinnedPosition,
                    ConstraintSet.PARENT_ID,
                    pinnedPosition,
                    root.resources.getDimension(R.dimen.stream_ui_spacing_small).toInt()
                )
                applyTo(root)
            }
        }
    }

    private fun getPinnedPosition(isMine: Boolean) =
        if (isMine) ConstraintSet.END else ConstraintSet.START
}
