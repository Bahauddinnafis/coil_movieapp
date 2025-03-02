package com.nafis.coilmovieapp.ui.watchlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.ui.home.components.MovieCoverImageList
import com.nafis.coilmovieapp.ui.home.defaultPadding
import com.nafis.coilmovieapp.ui.home.itemSpacing
import com.nafis.coilmovieapp.utils.K

@Composable
fun WatchlistScreen(
    onMovieClick: (id: Int) -> Unit,
    watchlistViewModel: WatchListViewModel = hiltViewModel()
) {
    val watchlist = watchlistViewModel.watchlist.collectAsState()
    val snackbarMessage = watchlistViewModel.snackbarMessage.collectAsState()

    LaunchedEffect(Unit) {
        watchlistViewModel.loadWatchlist()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage.value) {
        snackbarMessage.value?.let { message ->
            snackbarHostState.showSnackbar(message)
            watchlistViewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(defaultPadding)
        ) {
            items(watchlist.value) { item ->
                MovieCoverImageList(
                    movie = Movie(
                        id = item.id,
                        title = item.title,
                        posterPath = item.posterPath,
                        backdropPath = "",
                        genreIds = emptyList(),
                        originalLanguage = "",
                        originalTitle = "",
                        overview = "",
                        popularity = 0.0,
                        releaseDate = "",
                        video = false,
                        voteAverage = 0.0,
                        voteCount = 0
                    ),
                    onMovieClick = onMovieClick,
                    imageUrl = "${K.BASE_IMAGE_URL.replace("w500", "w185")}${item.posterPath}",
                    modifier = Modifier.padding(itemSpacing),
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}