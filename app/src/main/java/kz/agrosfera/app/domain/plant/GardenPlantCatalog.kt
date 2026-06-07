package kz.agrosfera.app.domain.plant

import androidx.annotation.DrawableRes
import kz.agrosfera.app.R

data class GardenPlant(
    val id: String,
    val name: String,
    val variety: String,
    val lastCheck: String,
    @DrawableRes val imageBackground: Int,
    val emoji: String,
)

object GardenPlantCatalog {
    val plants: List<GardenPlant> = listOf(
        GardenPlant("tomato", "Қызанақ", "Черри", "12.05.2026", R.drawable.bg_plant_tomato, "🍅"),
        GardenPlant("cucumber", "Қияр", "Зозуля", "10.05.2026", R.drawable.bg_plant_cucumber, "🥒"),
        GardenPlant("apple", "Алма", "Апорт", "08.05.2026", R.drawable.bg_plant_apple, "🍎"),
        GardenPlant("grape", "Жүзім", "Кешиш", "05.05.2026", R.drawable.bg_plant_grape, "🍇"),
    )
}
