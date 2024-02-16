package fr.isen.schwedt.androiderestaurant.dto

data class ItemCart(
    val item: Item,
    var count: HashMap<Int, Int>
)
