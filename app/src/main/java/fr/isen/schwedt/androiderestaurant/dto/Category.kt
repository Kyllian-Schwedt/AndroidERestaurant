package fr.isen.schwedt.androiderestaurant.dto

data class Category(
    val name_fr: String,
    val name_en: String,
    val items: List<Item>
)