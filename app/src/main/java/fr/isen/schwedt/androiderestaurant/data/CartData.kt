package fr.isen.schwedt.androiderestaurant.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableIntState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.schwedt.androiderestaurant.dto.Item
import fr.isen.schwedt.androiderestaurant.dto.ItemCart
import java.io.FileNotFoundException

class CartData private constructor() {
    var items: MutableList<ItemCart> = Companion.items
    var callbacks: MutableList<() -> Unit> = mutableListOf()

    fun persiste(context: Context) {
        val jsonString = Gson().toJson(items)
        context.openFileOutput("CartData.json", Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    companion object {
        private var isInitialized = false
        var items: MutableList<ItemCart> = mutableListOf()
        private var callbacks: MutableList<() -> Unit> = mutableListOf()

        private fun fromJson(json: String): MutableList<ItemCart> {
            val listType = object : TypeToken<MutableList<ItemCart>>() {}.type
            return Gson().fromJson(json, listType)
        }

        private fun loadFromFile(context: Context) {
            items = try {
                val jsonString = context.openFileInput("CartData.json").bufferedReader().use { it.readText() }
                fromJson(jsonString)
            } catch (e: FileNotFoundException) {
                mutableListOf()
            }
            isInitialized = true
        }

        private fun getCart(context: Context): CartData {
            return if (isInitialized) {
                CartData()
            } else {
                loadFromFile(context)
                CartData()
            }
        }

        fun getCartCount(context: Context): Int {
            if(!isInitialized)
                getCart(context)

            return items.sumOf { it.count.values.sum() } ?: 0
        }

        fun addItemToCart(item: Item, map: List<Int>, context: Context) {
            val existingItem = items.find { it.item.id == item.id }

            if (existingItem != null) {
                for (i in map.indices) {
                    existingItem.count[i] = existingItem.count.getOrDefault(i, 0) + map[i]
                }
            } else {
                val itemCart = ItemCart(item, HashMap())
                for (i in map.indices) {
                    itemCart.count[i] = map[i]
                }
                items.add(itemCart)
            }

            callbacks.forEach { it.invoke() }
            getCart(context).persiste(context)
        }

        fun updateCart() {
            callbacks.forEach { it.invoke() }
        }

        fun addCallback(callback: () -> Unit) {
            callbacks.add(callback)
        }

        fun removeCallback(callback: () -> Unit) {
            callbacks.remove(callback)
        }
    }
}