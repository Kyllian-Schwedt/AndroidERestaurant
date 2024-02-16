package fr.isen.schwedt.androiderestaurant.lib

enum class ContentTypes(val s: String, val title: String) {
    ENTREE("entree", "Entrées"), PLAT("plat", "Plats"), DESSERT("dessert", "Desserts");

    override fun toString(): String {
        return s
    }
    companion object {
        fun fromString(s: String): ContentTypes? {
            return when (s) {
                "entree" -> ENTREE
                "plat" -> PLAT
                "dessert" -> DESSERT
                else -> null
            }
        }
    }
}