package fr.isen.schwedt.androiderestaurant.compose

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.schwedt.androiderestaurant.data.CartData
import fr.isen.schwedt.androiderestaurant.page.CartActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    activity: Activity,
    navController: NavController? = null,
    returnBack: Boolean = false,
    title: String = "AndroidRestaurant",
    displayTitle: Boolean,
    openCart: Boolean = true
) {

    val itemCount = remember { mutableIntStateOf(CartData.getCartCount(activity)) }
    DisposableEffect(key1 = activity) {
        val callback = {
            itemCount.intValue = CartData.getCartCount(activity)
        }
        CartData.addCallback(callback)

        onDispose {
            CartData.removeCallback(callback)
        }
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.4f)),
        title = { if (displayTitle) Text(text = title) },
        navigationIcon = {
            if (returnBack) {
                IconButton(onClick = {
                    // Handle the back button
                    activity.finish()
                    navController?.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (openCart) {
                        val intent = Intent(activity, CartActivity::class.java)
                        activity.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .clip(CircleShape)
                    .size(64.dp)
            ) {
                BadgedBox(badge = { Badge { Text(text = itemCount.intValue.toString()) } }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}