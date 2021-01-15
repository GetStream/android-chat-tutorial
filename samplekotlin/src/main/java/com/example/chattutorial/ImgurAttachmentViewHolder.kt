package com.example.chattutorial

//import com.getstream.sdk.chat.adapter.AttachmentListItem
//import com.getstream.sdk.chat.adapter.viewholder.attachment.BaseAttachmentViewHolder
//import com.getstream.sdk.chat.view.MessageListView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import coil.load
import com.example.chattutorial.databinding.ListItemAttachImgurBinding
import com.getstream.sdk.chat.adapter.MessageListItem
import io.getstream.chat.android.ui.messages.adapter.BaseMessageItemViewHolder
import io.getstream.chat.android.ui.messages.adapter.MessageListItemPayloadDiff

class ImgurAttachmentViewHolder(
    parent: ViewGroup,
    private val binding: ListItemAttachImgurBinding = ListItemAttachImgurBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

//    override fun bind(attachmentListItem: AttachmentListItem) {
//        val background = bubbleHelper.getDrawableForAttachment(
//            messageItem.message,
//            messageItem.isMine,
//            messageItem.positions,
//            attachmentListItem.attachment
//        )
//        binding.ivMediaThumb.setShape(context, background)
//
//        binding.ivMediaThumb.load(attachmentListItem.attachment.thumbUrl) {
//            allowHardware(false)
//        }
//    }

    init {
        binding.ivMediaThumb.apply {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(
                    resources.getDimension(R.dimen.stream_ui_selected_attachment_corner_radius)
                )
                .build()
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        data.message.attachments.firstOrNull()?.imageUrl?.let {
            binding.ivMediaThumb.apply {
                load(it) {
                    allowHardware(false)
                    crossfade(true)
                    placeholder(R.drawable.stream_ui_picture_placeholder)
                }
            }

            ConstraintSet().apply {
                with(binding) {
                    clone(root)
                    val pinnedPosition = getPinnedPosition(data.isMine)
                    val clearedPosition = getPinnedPosition(!data.isMine)
                    clear(ivMediaThumb.id, clearedPosition)
                    connect(
                        ivMediaThumb.id,
                        pinnedPosition,
                        ConstraintSet.PARENT_ID,
                        pinnedPosition,
                        root.resources.getDimension(R.dimen.stream_ui_spacing_small).toInt()
                    )
                    applyTo(root)
                }
            }
        }
    }

    private fun getPinnedPosition(isMine: Boolean) =
        if (isMine) ConstraintSet.END else ConstraintSet.START
}
