package kz.agrosfera.app.ui.plants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.ItemPlantGardenBinding
import kz.agrosfera.app.domain.plant.GardenPlant

class GardenPlantAdapter(
    private val plants: List<GardenPlant>,
) : RecyclerView.Adapter<GardenPlantAdapter.VH>() {

    class VH(val binding: ItemPlantGardenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlantGardenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val plant = plants[position]
        val ctx = holder.itemView.context
        holder.binding.textPlantName.text = plant.name
        holder.binding.textVariety.text = ctx.getString(R.string.plant_variety, plant.variety)
        holder.binding.textLastCheck.text = ctx.getString(R.string.plant_last_check, plant.lastCheck)
        holder.binding.textEmoji.text = plant.emoji
        holder.binding.plantImageBg.setBackgroundResource(plant.imageBackground)
    }

    override fun getItemCount(): Int = plants.size
}
