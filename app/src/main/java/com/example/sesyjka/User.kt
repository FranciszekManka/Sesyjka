data class User(
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val age: Int? = null,
    val wydzial: String = "",
    val miasto: String = "",
    val photoUrl: String = "",
    val opis: String = "",
    val kierunek: String = "",
    val rok_studiow: String = ""
) {
    companion object {
        val wydzialy = listOf(
            "Wydział Architektury",
            "Wydział Automatyki, Elektroniki i Informatyki",
            "Wydział Budownictwa",
            "Wydział Chemiczny",
            "Wydział Elektryczny",
            "Wydział Górnictwa, Inżynierii Bezpieczeństwa i Automatyki Przemysłowej",
            "Wydział Inżynierii Biomedycznej",
            "Wydział Inżynierii Materiałowej",
            "Wydział Inżynierii Środowiska i Energetyki",
            "Wydział Matematyki Stosowanej",
            "Wydział Mechaniczny Technologiczny",
            "Wydział Organizacji i Zarządzania",
            "Wydział Transportu i Inżynierii Lotniczej",
            "Instytut Fizyki - Centrum Naukowo-Dydaktyczne"
        )

        val kierunkiMap = mapOf(
            "Wydział Architektury" to listOf("Architektura", "Architektura Wnętrz"),
            "Wydział Automatyki, Elektroniki i Informatyki" to listOf("Informatyka", "Automatyka i Robotyka", "Teleinformatyka", "Elektronika i Telekomunikacja", "Sztuczna Inteligencja i Data Science"),
            "Wydział Budownictwa" to listOf("Budownictwo", "Inżynieria Środowiska", "Inżynieria Lądowa"),
            "Wydział Chemiczny" to listOf("Technologia Chemiczna", "Inżynieria Chemiczna i Procesowa", "Biotechnologia"),
            "Wydział Elektryczny" to listOf("Elektrotechnika", "Informatyka w Energetyce", "Elektroenergetyka", "Inżynieria Elektryczna"),
            "Wydział Górnictwa, Inżynierii Bezpieczeństwa i Automatyki Przemysłowej" to listOf("Inżynieria Bezpieczeństwa", "Górnictwo i Geologia", "Geoinżynieria", "Inżynieria Procesowa", "Geodezja i Kartografia"),
            "Wydział Inżynierii Biomedycznej" to listOf("Inżynieria Biomedyczna"),
            "Wydział Inżynierii Materiałowej" to listOf("Inżynieria Materiałowa", "Nanotechnologia", "Materiały Funkcjonalne"),
            "Wydział Inżynierii Środowiska i Energetyki" to listOf("Energetyka", "Inżynieria Środowiska", "Ochrona Środowiska", "Zrównoważony Rozwój"),
            "Wydział Matematyki Stosowanej" to listOf("Matematyka Stosowana", "Analiza Danych", "Finanse i Rachunkowość"),
            "Wydział Mechaniczny Technologiczny" to listOf("Mechanika i Budowa Maszyn", "Mechatronika", "Inżynieria Produkcji", "Inżynieria Lotnicza i Kosmiczna"),
            "Wydział Organizacji i Zarządzania" to listOf("Zarządzanie", "Logistyka", "Inżynieria Zarządzania", "Zarządzanie i Inżynieria Produkcji"),
            "Wydział Transportu i Inżynierii Lotniczej" to listOf("Transport", "Inżynieria Transportu", "Inżynieria Lotnicza", "Logistyka Transportu"),
            "Instytut Fizyki - Centrum Naukowo-Dydaktyczne" to listOf("Fizyka Techniczna", "Fizyka Stosowana", "Nanotechnologia")
        )

        val kierunki = kierunkiMap.values.flatten().distinct()

        val lataStudiow = listOf(
            "1 rok", "2 rok", "3 rok", "4 rok", "5 rok", "6 rok"
        )
    }
}
