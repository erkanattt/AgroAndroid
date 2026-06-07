package kz.agrosfera.app.ui.plants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.databinding.ItemPlantChipBinding
import kz.agrosfera.app.domain.plant.GardenPlant

class PlantChipAdapter(
    private val plants: List<GardenPlant>,
) : RecyclerView.Adapter<PlantChipAdapter.VH>() {

    class VH(val binding: ItemPlantChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlantChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val plant = plants[position]
        holder.binding.textPlantName.text = plant.name
        holder.binding.textEmoji.text = plant.emoji
        holder.binding.plantImageBg.setBackgroundResource(plant.imageBackground)
    }

    override fun getItemCount(): Int = plants.size
}
