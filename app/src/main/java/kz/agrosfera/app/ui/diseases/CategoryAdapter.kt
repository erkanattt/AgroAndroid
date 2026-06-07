package kz.agrosfera.app.ui.diseases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.ItemCategoryBinding
import kz.agrosfera.app.domain.plant.DiseaseCategory
import kz.agrosfera.app.domain.plant.PlantDiseaseCatalog

class CategoryAdapter(
    private val categories: List<DiseaseCategory>,
    private val onClick: (DiseaseCategory) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    class VH(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = categories[position]
        val ctx = holder.itemView.context
        val count = PlantDiseaseCatalog.byCategory(category.id).size
        holder.binding.textEmoji.text = category.emoji
        holder.binding.textCategoryName.text = category.name
        holder.binding.textCategoryDesc.text = category.description
        holder.binding.textDiseaseCount.text = ctx.getString(R.string.category_disease_count, count)
        holder.binding.root.setOnClickListener { onClick(category) }
    }

    override fun getItemCount(): Int = categories.size
}
