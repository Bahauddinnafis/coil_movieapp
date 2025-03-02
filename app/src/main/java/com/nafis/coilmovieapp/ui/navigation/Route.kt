package com.nafis.coilmovieapp.ui.navigation

import com.nafis.coilmovieapp.utils.K

sealed class Route(val route: String) {
    data object HomeScreen : Route("home_screen")
    data object FilmScreen : Route("film_screen/{${K.MOVIE_ID}}") {
        fun getRouteWithArgs(id: Int): String {
            return "film_screen/$id"
        }
    }
    data object MovieListScreen : Route("movie_list_screen/{type}") {
        fun getRouteWithArgs(type: String): String {
            return "movie_list_screen/$type"
        }
    }
    data object WatchlistScreen : Route("watchlist_screen")
    data object FavoriteScreen : Route("favorite_screen")
    data object CastCrewScreen : Route("cast_crew_screen/{${K.MOVIE_ID}}") {
        fun getRouteWithArgs(id: Int): String {
            return "cast_crew_screen/$id"
        }
    }
}