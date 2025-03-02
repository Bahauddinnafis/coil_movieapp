package com.nafis.coilmovieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.ShapeCornerRadius
import com.exyte.animatednavbar.utils.toPxf
import com.nafis.coilmovieapp.ui.home.HomeViewModel
import com.nafis.coilmovieapp.ui.navigation.MovieNavigationGraph
import com.nafis.coilmovieapp.ui.navigation.NavigationBarItem
import com.nafis.coilmovieapp.ui.navigation.Route
import com.nafis.coilmovieapp.ui.theme.MovieAppCoilTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppCoilTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        val navController = rememberNavController()
        val homeViewModel: HomeViewModel = hiltViewModel()
        var selectedIndex by remember { mutableStateOf(0) }

        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                selectedIndex = when (backStackEntry.destination.route) {
                    Route.HomeScreen.route -> 0
                    Route.WatchlistScreen.route -> 1
                    Route.FavoriteScreen.route -> 2
                    else -> selectedIndex
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AnimatedNavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    selectedIndex = selectedIndex,
                    barColor = MaterialTheme.colorScheme.primaryContainer,
                    ballColor = MaterialTheme.colorScheme.primary,
                    cornerRadius = ShapeCornerRadius(
                        bottomLeft = 16.dp.toPxf(),
                        bottomRight = 16.dp.toPxf(),
                        topLeft = 16.dp.toPxf(),
                        topRight = 16.dp.toPxf()
                    ),
                    ballAnimation = Parabolic(tween(300)),
                    indentAnimation = Height(tween(300))
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            title = { Text(text = item.title) },
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                when (index) {
                                    0 -> navController.navigate(Route.HomeScreen.route)
                                    1 -> navController.navigate(Route.WatchlistScreen.route)
                                    2 -> navController.navigate(Route.FavoriteScreen.route)
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            MovieNavigationGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                homeViewModel = homeViewModel
            )
        }
    }
}

private val items = listOf(
    NavigationItem("Home", Icons.Default.Home),
    NavigationItem("Watchlist", Icons.Default.List),
    NavigationItem("Favorite", Icons.Default.Favorite)
)

data class NavigationItem(
    val title: String,
    val icon: ImageVector
)