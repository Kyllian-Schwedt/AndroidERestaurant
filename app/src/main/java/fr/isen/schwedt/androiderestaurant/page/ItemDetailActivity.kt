package fr.isen.schwedt.androiderestaurant.page

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import fr.isen.schwedt.androiderestaurant.compose.CustomTopBar
import fr.isen.schwedt.androiderestaurant.data.CartData
import fr.isen.schwedt.androiderestaurant.dto.Ingredient
import fr.isen.schwedt.androiderestaurant.dto.Item
import fr.isen.schwedt.androiderestaurant.dto.Price
import fr.isen.schwedt.androiderestaurant.ui.theme.AndroidERestaurantTheme


class ItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val item = getItemFromIntent()
                    ItemDetails(item = item, activity = this, hasValidUnsplashKey = true)
                }
            }
        }
    }

    private fun getItemFromIntent(): Item {
        val bundle: Bundle? = intent.extras
        val itemJson = bundle?.getString("item")
        return Gson().fromJson(itemJson, Item::class.java)
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ItemDetails(
    item: Item,
    activity: ItemDetailActivity,
    hasValidUnsplashKey: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CustomTopBar(activity = activity, returnBack = true, displayTitle = false)
        },
        content = {
            ItemDetailsContent(item, imageHeight = 300.dp, activity = activity)
        }
    )
}


@Composable
private fun ItemDetailsContent(item: Item, imageHeight: Dp, activity: ItemDetailActivity){
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ItemImage(
            imagesUrls = item.images,
            imageHeight = imageHeight,
            modifier = Modifier
                .fillMaxWidth()
        )
        ItemInformation(item = item, context = activity.applicationContext)
    }
}

@Composable
private fun ItemInformation(
    item: Item,
    context: Context,
    modifier: Modifier = Modifier
) {
    val priceCounts = remember { item.prices.map { mutableIntStateOf(0) } }
    Column(modifier = modifier.padding(32.dp)) {
        Text(
            text = item.name_en,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 8.dp
                )
                .align(Alignment.CenterHorizontally)
        )
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = "Category ID: ${item.id_category}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Type : ${item.categ_name_fr}",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray
        )
        

        Text(
            text = "Ingredients: ${item.ingredients.joinToString { it.name_en }}",
            modifier = Modifier
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
        item.prices.forEachIndexed { index, price ->
            Row(
                modifier = Modifier
                    .clickable { }
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Price
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
                     Button(onClick = { if (priceCounts[index].intValue > 0) priceCounts[index].intValue-- }) {
                         Text(text = "-")
                     }

                     // Display the count
                     Text(text = "${priceCounts[index].intValue}", modifier = Modifier.padding(8.dp))

                     // Increase button
                     Button(onClick = { priceCounts[index].intValue++ }) {
                         Text(text = "+")
                     }
                 }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Total price and add to cart button
        val totalPrice = priceCounts.mapIndexed { index, count ->
            count.intValue * item.prices[index].price.toFloat()
        }.sum()
        Text(
            text = "$totalPrice €",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Button(
            onClick = {
                      CartData.addItemToCart(item, priceCounts.map { it.intValue }, context)
                      },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f) // Make the button take 80% of the width of the page
        ) {
            Text(text = "Add to cart")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemImage(
    imagesUrls: List<String>,
    imageHeight: Dp,
    modifier: Modifier = Modifier
) {
    var imageIndex by remember { mutableIntStateOf(0) }
    val transition = updateTransition(targetState = imagesUrls.firstOrNull(), label = "imageTransition")
    val image = transition.animateDp(label = "imageTransition") { imageUrl ->
        if (imageUrl != null) {
            imageHeight
        } else {
            0.dp
        }
    }
    val imageAlpha = transition.animateFloat(label = "imageTransition") { imageUrl ->
        if (imageUrl != null) {
            1f
        } else {
            0f
        }
    }
    val imageModifier = modifier
        .height(image.value)
        .alpha(imageAlpha.value)

    val pagerState = rememberPagerState(pageCount = {
        imagesUrls.size
    })

    Box(modifier = imageModifier) {
        if (imagesUrls.isNotEmpty()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                Image(
                    painter = rememberAsyncImagePainter(imagesUrls[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val dummyIngredients = listOf(
        Ingredient("1", "1", "Tomato", "Tomate", "2022-01-01", "2022-01-01", "1"),
        Ingredient("2", "1", "Cheese", "Fromage", "2022-01-01", "2022-01-01", "1"),
        Ingredient("3", "1", "Bread", "Pain", "2022-01-01", "2022-01-01", "1")
    )

    val dummyPrices = listOf(
        Price("1", "1", "1", "10.0", "2022-01-01", "2022-01-01", "Small"),
        Price("2", "1", "2", "15.0", "2022-01-01", "2022-01-01", "Medium"),
        Price("3", "1", "3", "20.0", "2022-01-01", "2022-01-01", "Large")
    )

    val dummyItem = Item(
        id = "1",
        name_fr = "Sandwich",
        name_en = "Sandwich",
        id_category = "1",
        categ_name_fr = "Plat",
        categ_name_en = "Main Course",
        images = listOf("https://cdn.pixabay.com/photo/2016/05/05/02/37/sunset-1373171_1280.jpg", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQHt_1s2UF2ojIK1W42V4uUuS2aBj7a-Pp-Qo6eSl650ow9PmcbOV_1bwkhZ7rs2Ak58EI&usqp=CAU"),
        ingredients = dummyIngredients,
        prices = dummyPrices
    )

    AndroidERestaurantTheme {
        ItemDetails(
            item = dummyItem,
            activity = ItemDetailActivity(),
            hasValidUnsplashKey = true
        )
    }
}