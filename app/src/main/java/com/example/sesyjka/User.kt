data class User(
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val wydzial: String = "",
    val miasto: String = "",
    val photoUrl: String = "",
    val opis: String = ""
) {
    companion object {
        // statyczna lista wydziałów PS
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
    }
}
