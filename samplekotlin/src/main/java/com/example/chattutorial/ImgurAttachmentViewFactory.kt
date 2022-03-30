package com.example.chattutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.example.chattutorial.databinding.AttachmentImgurBinding
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.AttachmentFactory
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.InnerAttachmentViewHolder

/** A custom attachment factory to show an imgur logo if the attachment URL is an imgur image. */
class ImgurAttachmentFactory : AttachmentFactory {

    override fun canHandle(message: Message): Boolean {
        val imgurAttachment = message.attachments.firstOrNull { it.isImgurAttachment() }
        return imgurAttachment != null
    }

    override fun createViewHolder(
        message: Message,
        listeners: MessageListListenerContainer?,
        parent: ViewGroup
    ): InnerAttachmentViewHolder {
        val imgurAttachment = message.attachments.firstOrNull { it.isImgurAttachment() }
            ?: return createViewHolder(message, listeners, parent)
        val binding = AttachmentImgurBinding
            .inflate(LayoutInflater.from(parent.context), null, false)
        return ImgurAttachmentViewHolder(
            imgurAttachment = imgurAttachment,
            binding = binding
        )
    }

    private fun Attachment.isImgurAttachment(): Boolean = imageUrl?.contains("imgur") == true

    private class ImgurAttachmentViewHolder(
        binding: AttachmentImgurBinding,
        imgurAttachment: Attachment
    ) :
        InnerAttachmentViewHolder(binding.root) {

        init {
            binding.ivMediaThumb.apply {
                shapeAppearanceModel = shapeAppearanceModel
                    .toBuilder()
                    .setAllCornerSizes(resources.getDimension(R.dimen.stream_ui_selected_attachment_corner_radius))
                    .build()
                load(imgurAttachment.imageUrl) {
                    allowHardware(false)
                    crossfade(true)
                    placeholder(R.drawable.stream_ui_picture_placeholder)
                }
            }
        }
    }
}