package kz.agrosfera.app.domain.weather

object KazakhstanCityCatalog {
    val cities: List<KazakhstanCity> = listOf(
        KazakhstanCity("almaty", "Алматы", 43.2566, 76.9286),
        KazakhstanCity("astana", "Астана", 51.1694, 71.4491),
        KazakhstanCity("shymkent", "Шымкент", 42.3417, 69.5901),
        KazakhstanCity("karaganda", "Қарағанды", 49.8047, 73.1094),
        KazakhstanCity("aktobe", "Актобе", 50.2839, 57.1669),
        KazakhstanCity("taraz", "Тараз", 42.9000, 71.3667),
        KazakhstanCity("pavlodar", "Павлодар", 52.2873, 76.9674),
        KazakhstanCity("ust_kamenogorsk", "Өскемен", 49.9480, 82.6289),
        KazakhstanCity("semey", "Семей", 50.4111, 80.2275),
        KazakhstanCity("atyrau", "Атырау", 47.1164, 51.9200),
        KazakhstanCity("kostanay", "Қостанай", 53.2144, 63.6246),
        KazakhstanCity("petropavl", "Петропavl", 54.8753, 69.1620),
        KazakhstanCity("oral", "Орал", 51.2333, 51.3667),
        KazakhstanCity("turkistan", "Түркістан", 43.2973, 68.2517),
        KazakhstanCity("kyzylorda", "Қызылорда", 44.8479, 65.5093),
        KazakhstanCity("aktau", "Ақтау", 43.6500, 51.1600),
        KazakhstanCity("kokshetau", "Кокшетau", 53.2833, 69.3833),
        KazakhstanCity("taldykorgan", "Талдықорған", 45.0167, 78.3667),
    )

    fun byId(id: String): KazakhstanCity? = cities.firstOrNull { it.id == id }

    fun defaultCity(): KazakhstanCity = cities.first()
}
