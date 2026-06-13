package kz.agrosfera.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.ItemRecentHomeBinding
import kz.agrosfera.app.domain.diagnosis.DiagnosisRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeRecentAdapter(
    private val items: List<DiagnosisRecord>,
) : RecyclerView.Adapter<HomeRecentAdapter.VH>() {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    class VH(val binding: ItemRecentHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecentHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context
        holder.binding.textTitle.text = item.displayName
        holder.binding.textMeta.text = item.confidencePercent?.let {
            ctx.getString(R.string.diagnosis_confidence, it)
        } ?: ctx.getString(R.string.result_healthy)
        holder.binding.textDate.text = dateFormat.format(Date(item.timestampMs))
    }

    override fun getItemCount(): Int = items.size
}
