package kz.agrosfera.app.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.ItemChatMessageBinding
import kz.agrosfera.app.domain.chat.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.VH>() {

    private val items = mutableListOf<ChatMessage>()

    fun submit(messages: List<ChatMessage>) {
        items.clear()
        items.addAll(messages)
        notifyDataSetChanged()
    }

    class VH(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = items[position]
        val ctx = holder.itemView.context
        holder.binding.textMessage.text = msg.text
        if (msg.isUser) {
            holder.binding.textMessage.updateLayoutParams<FrameLayout.LayoutParams> {
                gravity = Gravity.END
            }
            holder.binding.textMessage.setBackgroundResource(R.drawable.bg_chat_user)
        } else {
            holder.binding.textMessage.updateLayoutParams<FrameLayout.LayoutParams> {
                gravity = Gravity.START
            }
            holder.binding.textMessage.setBackgroundResource(R.drawable.bg_chat_bot)
        }
        holder.binding.textMessage.setTextColor(
            ContextCompat.getColor(ctx, R.color.text_primary),
        )
    }

    override fun getItemCount(): Int = items.size
}
