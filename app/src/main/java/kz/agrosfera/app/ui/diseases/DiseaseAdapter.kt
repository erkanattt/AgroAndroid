package kz.agrosfera.app.ui.diseases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.databinding.ItemDiseaseBinding
import kz.agrosfera.app.domain.plant.PlantDisease

class DiseaseAdapter(
    private val onClick: (PlantDisease) -> Unit,
) : ListAdapter<PlantDisease, DiseaseAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemDiseaseBinding,
        private val onClick: (PlantDisease) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlantDisease) {
            binding.textRank.text = "#${item.frequencyRank}"
            binding.textTitle.text = item.name
            binding.textSummary.text = item.summary
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PlantDisease>() {
            override fun areItemsTheSame(oldItem: PlantDisease, newItem: PlantDisease) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlantDisease, newItem: PlantDisease) =
                oldItem == newItem
        }
    }
}
