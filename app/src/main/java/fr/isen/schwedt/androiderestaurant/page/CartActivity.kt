package fr.isen.schwedt.androiderestaurant.page

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import fr.isen.schwedt.androiderestaurant.R
import fr.isen.schwedt.androiderestaurant.compose.CustomTopBar
import fr.isen.schwedt.androiderestaurant.data.CartData
import fr.isen.schwedt.androiderestaurant.data.CartData.Companion.items
import fr.isen.schwedt.androiderestaurant.dto.ItemCart
import fr.isen.schwedt.androiderestaurant.ui.theme.AndroidERestaurantTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartList(activity = this)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CartList(activity: Activity, modifier: Modifier = Modifier) {
    var isRefreshing by remember { mutableStateOf(false) }
    var total by remember { mutableDoubleStateOf(calculateTotal(CartData.items)) }

    val totalItemCount by remember {
        derivedStateOf {
            CartData.items.sumOf { it.count.values.sum() }
        }
    }

    LaunchedEffect(totalItemCount) {
        total = calculateTotal(CartData.items)
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            isRefreshing = false
        },
        content = {
            Scaffold(
                topBar = {
                    CustomTopBar(
                        activity = activity,
                        returnBack = true,
                        title = "Cart",
                        displayTitle = true,
                        openCart = false
                    )
                },
                content = {
                    if (items.size != 0) {
                        LazyColumn(modifier = Modifier.padding(top = 70.dp)) {
                            items(items) { item ->
                                ItemCartComposable(item, activity, onClick = {
                                    Log.d("CartActivity", "Item clicked: ${item.item.name_fr}")
                                }, upDateTotal = {

                                })
                            }
                        }
                    } else {
                        Text(text = "No items in cart")
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { /* Implement "Buy" functionality here */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(text = "Total: € $total")
                        }
                    }
                }
            )
        }
    )
}
private fun calculateTotal(items: List<ItemCart>): Double {

    return items.sumOf { item ->
        val priceCounts = item.count.values.toList()
        val totalItemPrice = priceCounts.mapIndexed { index, count ->
            count.toInt() * item.item.prices[index].price.toDouble()
        }.sum()
        return@sumOf totalItemPrice
    }
}

@Composable
fun ItemCartComposable(
    item: ItemCart,
    activity: Activity,
    onClick: () -> Unit,
    upDateTotal: () -> Unit
) {
    var imageIndex by remember { mutableIntStateOf(0) }
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = androidx.compose.ui.Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        val priceCounts = remember { item.count.values.map { mutableIntStateOf(it) } }
        Column(Modifier.fillMaxWidth()) {
            val imageUrl = item.item.images.getOrNull(imageIndex)
            if (imageUrl != null) {
                val painter = rememberAsyncImagePainter(
                    model = imageUrl
                )
                Image(
                    painter = painter,
                    contentDescription = "test",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.item_image_height)),
                    contentScale = ContentScale.Crop
                )
                when (painter.state) {
                    is AsyncImagePainter.State.Error -> {
                        if (imageIndex < item.item.images.size - 1) {
                            imageIndex++
                        }
                    }
                    else -> {}
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = item.item.name_en,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 4.dp,
                            bottom = 8.dp
                        )
                        .align(Alignment.CenterHorizontally)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                Text(
                    text = "Prices",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                item.item.prices.forEachIndexed { index, price ->
                    Row(
                        modifier = Modifier
                            .clickable { }
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Price: ${price.price}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Row (verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            // Decrease button
                            Button(onClick = {
                                if (priceCounts[index].intValue > 0) {
                                    priceCounts[index].intValue--
                                    item.count[index] = priceCounts[index].intValue // Update the item count map
                                    CartData.updateItemCount(item.item, activity)
                                    upDateTotal() // Call the function to update the total
                                }
                            }) {
                                Text(text = "-")
                            }

                            // Display the count
                            Text(text = "${priceCounts[index].intValue}", modifier = Modifier.padding(8.dp))

                            // Increase button
                            Button(onClick = {
                                priceCounts[index].intValue++
                                item.count[index] = priceCounts[index].intValue
                                CartData.updateItemCount(item.item, activity)
                                upDateTotal()
                            }) {
                                Text(text = "+")
                            }
                        }
                    }
                }
                IconButton(onClick = {
                    CartData.removeItemFromCart(item.item, activity)
                    upDateTotal()
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove item")
                }
            }
        }
    }
}