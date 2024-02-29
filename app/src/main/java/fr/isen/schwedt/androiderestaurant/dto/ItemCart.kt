package fr.isen.schwedt.androiderestaurant.dto

import androidx.compose.runtime.snapshots.SnapshotStateMap

data class ItemCart(
    val item: Item,
    var count: SnapshotStateMap<Int, Int>
)
