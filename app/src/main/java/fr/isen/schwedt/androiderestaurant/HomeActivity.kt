package fr.isen.schwedt.androiderestaurant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.schwedt.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.schwedt.androiderestaurant.compose.CustomTopBar
import fr.isen.schwedt.androiderestaurant.lib.ContentTypes
import fr.isen.schwedt.androiderestaurant.page.CategoryActivity

class HomeActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(name = "RestoAPP", activity = this)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "MainActivity is being destroyed")
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, activity: ComponentActivity) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CustomTopBar(activity = activity, navController = rememberNavController(), displayTitle = true)
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Add the logo at the top of the list
                    Image(
                        painter = painterResource(id = R.drawable.undraw_chef_cu_0_r),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp) // adjust the size as needed
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 100.dp)
                            .clickable {
                                val intent = Intent(context, CategoryActivity::class.java)
                                intent.putExtra("category", ContentTypes.ENTREE.toString())
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = "Entrée",
                            modifier = Modifier
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                    // Add a separator
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 100.dp)
                            .clickable {
                                val intent = Intent(context, CategoryActivity::class.java)
                                intent.putExtra("category", ContentTypes.PLAT.toString())
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = "Plat",
                            modifier = Modifier
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                    // Add a spacer
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 100.dp)
                            .clickable {
                                val intent = Intent(context, CategoryActivity::class.java)
                                intent.putExtra("category", ContentTypes.DESSERT.toString())
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = "Dessert",
                            modifier = Modifier
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        })
}

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val context = LocalContext.current
            val intent = Intent(context, HomeActivity::class.java)
        }

        composable("entree") {
            val context = LocalContext.current
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("category", "entree")
            context.startActivity(intent)
        }
        composable("plat") {
            val context = LocalContext.current
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("category", "plat")
            context.startActivity(intent)
        }
        composable("dessert") {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        Greeting("Android", activity = HomeActivity())
    }
}