package kz.agrosfera.app.util

object WeatherCodeMapper {
    data class Condition(val label: String, val emoji: String)

    fun map(code: Int): Condition = when (code) {
        0 -> Condition("Күн ашық", "☀️")
        1 -> Condition("Негізінен ашық", "🌤️")
        2 -> Condition("Бұлтты", "⛅")
        3 -> Condition("Тұманды", "☁️")
        45, 48 -> Condition("Тұман", "🌫️")
        51, 53, 55 -> Condition("Сиреньді жауын", "🌦️")
        56, 57 -> Condition("Сиреньді қатып жауын", "🌧️")
        61, 63, 65 -> Condition("Жауын", "🌧️")
        66, 67 -> Condition("Қатып жауын", "🌨️")
        71, 73, 75 -> Condition("Қар", "❄️")
        77 -> Condition("Қар дана", "❄️")
        80, 81, 82 -> Condition("Нөсер", "🌦️")
        85, 86 -> Condition("Қар жауын", "🌨️")
        95 -> Condition("Найзағай", "⛈️")
        96, 99 -> Condition("Найзағай мен бұршақ", "⛈️")
        else -> Condition("Ауа-райы", "🌡️")
    }
}
