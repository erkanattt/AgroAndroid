package kz.agrosfera.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.databinding.ItemMenuGridBinding

class HomeMenuAdapter(
    private val items: List<HomeMenuItem>,
    private val onClick: (HomeMenuItem) -> Unit,
) : RecyclerView.Adapter<HomeMenuAdapter.VH>() {

    class VH(val binding: ItemMenuGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMenuGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.textEmoji.text = item.emoji
        holder.binding.textTitle.setText(item.titleRes)
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        fun spanSizeLookup(): GridLayoutManager.SpanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = 1
            }
    }
}
