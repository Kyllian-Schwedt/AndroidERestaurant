package fr.isen.schwedt.androiderestaurant.data

import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
        val encryptedData = CryptoUtils.encrypt(jsonString)
        context.openFileOutput("CartData.json", Context.MODE_PRIVATE).use {
            it.write(encryptedData.toByteArray())
        }
    }

    companion object {
        private var isInitialized = false
        var items: SnapshotStateList<ItemCart> = SnapshotStateList()
        private var callbacks: MutableList<() -> Unit> = mutableListOf()

        private fun fromJson(json: String): MutableList<ItemCart> {
            val listType = object : TypeToken<MutableList<ItemCart>>() {}.type
            return Gson().fromJson(json, listType)
        }

        private fun loadFromFile(context: Context) {
            val loadedItems = try {
                val encryptedData = context.openFileInput("CartData.json").bufferedReader().use { it.readText() }
                val decryptedData = CryptoUtils.decrypt(encryptedData)
                fromJson(decryptedData)
            } catch (e: FileNotFoundException) {
                mutableListOf()
            }
            items = SnapshotStateList()
            items.addAll(loadedItems)
            isInitialized = true
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

        fun updateItemCount(item: Item, context: Context) {
            val existingItem = items.find { it.item.id == item.id }

            if (existingItem != null) {
                callbacks.forEach { it.invoke() }
                getCart(context).persiste(context)
            }

            callbacks.forEach { it.invoke() }
            getCart(context).persiste(context)
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
                val itemCart = ItemCart(item, SnapshotStateMap())
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

        @Deprecated("Use State of the list instead")
        fun addCallback(callback: () -> Unit) {
            callbacks.add(callback)
        }

        @Deprecated("Use State of the list instead")
        fun removeCallback(callback: () -> Unit) {
            callbacks.remove(callback)
        }

        fun removeItemFromCart(item: Item, activity: Context) {
            val existingItem = items.find { it.item.id == item.id }
            if (existingItem != null) {
                items.remove(existingItem)
            }
            updateCart()
            getCart(activity).persiste(activity)
        }
    }
}