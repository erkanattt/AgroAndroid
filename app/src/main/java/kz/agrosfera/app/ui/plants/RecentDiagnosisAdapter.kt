package kz.agrosfera.app.ui.plants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.R
import kz.agrosfera.app.data.local.LastDiagnosis
import kz.agrosfera.app.databinding.ItemRecentDiagnosisBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentDiagnosisAdapter(
    private val items: List<LastDiagnosis>,
) : RecyclerView.Adapter<RecentDiagnosisAdapter.VH>() {

    class VH(val binding: ItemRecentDiagnosisBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecentDiagnosisBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.textDiagnosisName.text = item.displayName
        val date = dateFormat.format(Date(item.timestampMs))
        val conf = item.confidencePercent?.let {
            holder.itemView.context.getString(R.string.diagnosis_confidence, it)
        } ?: ""
        holder.binding.textDiagnosisDate.text = "$date · $conf"
    }

    override fun getItemCount(): Int = items.size
}
