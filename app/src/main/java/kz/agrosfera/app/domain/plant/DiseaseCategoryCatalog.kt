package kz.agrosfera.app.domain.plant

data class DiseaseCategory(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
)

object DiseaseCategoryCatalog {
    val categories: List<DiseaseCategory> = listOf(
        DiseaseCategory(
            id = "tomato",
            name = "Қызанақ",
            description = "Жиі кездесетін қызанақ аурулары",
            emoji = "🍅",
        ),
        DiseaseCategory(
            id = "cucumber",
            name = "Қияр",
            description = "Жиі кездесетін қияр аурулары",
            emoji = "🥒",
        ),
        DiseaseCategory(
            id = "potato",
            name = "Картоп",
            description = "Жиі кездесетін картоп аурулары",
            emoji = "🥔",
        ),
        DiseaseCategory(
            id = "pepper",
            name = "Бұрыш",
            description = "Жиі кездесетін бұрыш аурулары",
            emoji = "🫑",
        ),
        DiseaseCategory(
            id = "apple",
            name = "Алма",
            description = "Жиі кездесетін алма ағашы аурулары",
            emoji = "🍎",
        ),
        DiseaseCategory(
            id = "grape",
            name = "Жүзім",
            description = "Жиі кездесетін жүзім аурулары",
            emoji = "🍇",
        ),
        DiseaseCategory(
            id = "cabbage",
            name = "Қырыққабат",
            description = "Жиі кездесетін қырыққабат аурулары",
            emoji = "🥬",
        ),
        DiseaseCategory(
            id = "carrot",
            name = "Сабиз",
            description = "Жиі кездесетін сабиз аурулары",
            emoji = "🥕",
        ),
    )

    fun byId(id: String): DiseaseCategory? = categories.firstOrNull { it.id == id }
}
