package kz.agrosfera.app.ui.home

import androidx.annotation.IdRes
import kz.agrosfera.app.R

data class HomeMenuItem(
    val emoji: String,
    val titleRes: Int,
    @param:IdRes val action: Int,
)

object HomeMenuCatalog {
    val items = listOf(
        HomeMenuItem("📷", R.string.menu_identify, R.id.nav_check),
        HomeMenuItem("💬", R.string.menu_chat, R.id.nav_chat),
        HomeMenuItem("📖", R.string.menu_disease_info, R.id.nav_knowledge),
        HomeMenuItem("📅", R.string.menu_calendar, R.id.action_calendar),
        HomeMenuItem("💡", R.string.menu_tips, R.id.action_tips),
        HomeMenuItem("👤", R.string.menu_profile, R.id.nav_profile),
    )
}
