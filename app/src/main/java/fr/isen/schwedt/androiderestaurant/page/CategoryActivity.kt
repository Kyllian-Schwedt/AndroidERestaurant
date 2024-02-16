package fr.isen.schwedt.androiderestaurant.page

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import fr.isen.schwedt.androiderestaurant.R
import fr.isen.schwedt.androiderestaurant.compose.CustomTopBar
import fr.isen.schwedt.androiderestaurant.dto.Item
import fr.isen.schwedt.androiderestaurant.lib.ContentTypes
import fr.isen.schwedt.androiderestaurant.services.WebService
import fr.isen.schwedt.androiderestaurant.ui.theme.AndroidERestaurantTheme


class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("category")
        Log.d("CategoryActivity", "Category: $category")
        val ct : ContentTypes? = category?.let { ContentTypes.fromString(it) }
        val intent = Intent(this, WebService::class.java).apply {
            putExtra("category", category)
        }
        startService(intent)
        val webService = WebService()
        if(ct != null) {
            webService.fetchMenu(this, ct.title) { items ->
                setContent {
                    AndroidERestaurantTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            CategoryList(this, ct, items)
                        }
                    }
                }
            }
        }

        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (category != null) {
                        val contentTypes = try {
                            ContentTypes.fromString(category)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                        CategoryList(this, contentTypes, emptyList())
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoryList(activity: CategoryActivity, contentTypes: ContentTypes?, items: List<Item>, modifier: Modifier = Modifier) {
    var isRefreshing by remember { mutableStateOf(false) }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            isRefreshing = true
            val webService = WebService()
            if(contentTypes != null) {
                webService.fetchMenu(activity, contentTypes.title, true) {
                    isRefreshing = false
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    CustomTopBar(
                        activity = activity,
                        returnBack = true,
                        title = contentTypes?.title ?: "Invalid",
                        displayTitle = true
                    )
                },
                content = {
                    if (contentTypes != null) {
                        LazyColumn(modifier = Modifier.padding(top = 100.dp)) {
                            items(items) { item ->
                                ItemComposable(item, activity, onClick = {
                                    Log.d("CategoryActivity", "Item clicked: ${item.name_fr}")
                                })
                            }
                        }
                    } else {
                        Text(text = "Invalid category")
                    }
                }
            )
        }
    )
}

@Composable
fun ItemComposable(item: Item, activity: ComponentActivity, onClick: () -> Unit) {
    ImageListItem(name = item.name_fr, imageUrls = item.images, onClick = {
        val intent = Intent(activity, ItemDetailActivity::class.java)
        val itemJson = Gson().toJson(item)
        intent.putExtra("item", itemJson)
        activity.startActivity(intent)
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListItem(name: String, imageUrls: List<String>, onClick: () -> Unit) {
    var imageIndex by remember { mutableIntStateOf(0) }
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        Column(Modifier.fillMaxWidth()) {
            val imageUrl = imageUrls.getOrNull(imageIndex)
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
                        if (imageIndex < imageUrls.size - 1) {
                            imageIndex++
                        }
                    }
                    else -> {}
                }
            }
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.margin_normal))
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}