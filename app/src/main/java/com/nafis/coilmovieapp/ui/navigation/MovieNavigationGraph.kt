package com.nafis.coilmovieapp.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nafis.coilmovieapp.ui.detail.MovieDetailScreen
import com.nafis.coilmovieapp.ui.detail.components.CastCrewScreen
import com.nafis.coilmovieapp.ui.favorite.FavoriteScreen
import com.nafis.coilmovieapp.ui.home.HomeScreen
import com.nafis.coilmovieapp.ui.home.HomeViewModel
import com.nafis.coilmovieapp.ui.home.components.MovieListScreen
import com.nafis.coilmovieapp.ui.watchlist.WatchlistScreen
import com.nafis.coilmovieapp.utils.K

@Composable
fun MovieNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
) {
    val homeState = homeViewModel.homeState.collectAsStateWithLifecycle().value

    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen.route, // Perbaikan di sini
        modifier = modifier.fillMaxSize()
    ) {
        composable(
            route = Route.HomeScreen.route, // Perbaikan di sini
            enterTransition = { fadeIn() + scaleIn() },
            exitTransition = { fadeOut() + shrinkOut() }
        ) {
            HomeScreen(
                onMovieClick = {
                    navController.navigate(
                        Route.FilmScreen.getRouteWithArgs(id = it) // Perbaikan di sini
                    ) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                    }
                },
                onDiscoverMoviesClick = {
                    navController.navigate(Route.MovieListScreen.getRouteWithArgs("discoverMovies")) { // Perbaikan di sini
                        launchSingleTop = true
                    }
                },
                onTrendingMoviesClick = {
                    navController.navigate(Route.MovieListScreen.getRouteWithArgs("trendingMovies")) { // Perbaikan di sini
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Route.FilmScreen.route,
            arguments = listOf(navArgument(name = K.MOVIE_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(K.MOVIE_ID) ?: return@composable
            MovieDetailScreen(
                onNavigateUp = { navController.navigateUp() },
                onMovieClick = {
                    navController.navigate(
                        Route.FilmScreen.getRouteWithArgs(id = it) // Perbaikan di sini
                    ) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                    }
                },
                onActorClick = {

                },
                onDiscoverActor = {
                    navController.navigate(Route.CastCrewScreen.getRouteWithArgs(movieId))
                },
            )
        }
        composable(
            route = Route.MovieListScreen.route, // Perbaikan di sini
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val movies = when (type) {
                "discoverMovies" -> homeState.discoverMovies
                "trendingMovies" -> homeState.trendingMovies
                else -> emptyList()
            }

            val imageSize = if (type == "discoverMovies") "w154/" else "w185/"

            MovieListScreen(
                movies = movies,
                onMovieClick = { movieId ->
                    navController.navigate(Route.FilmScreen.getRouteWithArgs(movieId)) { // Perbaikan di sini
                        launchSingleTop = true
                    }
                },
                imageSize = imageSize,
                onBackClick = { navController.popBackStack() }
            )
        }
        // Watchlist Screen
        composable(
            route = Route.WatchlistScreen.route // Perbaikan di sini
        ) {
            WatchlistScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Route.FilmScreen.getRouteWithArgs(movieId)) { // Perbaikan di sini
                        launchSingleTop = true
                    }
                }
            )
        }
        // Favorite Screen
        composable(
            route = Route.FavoriteScreen.route // Perbaikan di sini
        ) {
            FavoriteScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Route.FilmScreen.getRouteWithArgs(movieId)) { // Perbaikan di sini
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Route.CastCrewScreen.route,
            arguments = listOf(navArgument(name = K.MOVIE_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(K.MOVIE_ID) ?: return@composable
            CastCrewScreen(
                onNavigateUp = { navController.navigateUp() },
                onActorClick = {}
            )
        }
    }
}