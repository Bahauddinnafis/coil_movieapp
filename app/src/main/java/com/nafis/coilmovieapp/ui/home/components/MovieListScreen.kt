package com.nafis.coilmovieapp.ui.home.components

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.ui.CustomSnackbar
import com.nafis.coilmovieapp.ui.favorite.FavoriteViewModel
import com.nafis.coilmovieapp.ui.watchlist.WatchListViewModel
import com.nafis.coilmovieapp.utils.K

@Composable
fun MovieListScreen(
    modifier: Modifier = Modifier,
    movies: List<Movie>,
    onMovieClick: (id: Int) -> Unit,
    imageSize: String,
    onBackClick: () -> Unit,
    watchListViewModel: WatchListViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val favoriteSnackbarMessage by favoriteViewModel.snackbarMessage.collectAsState()
    val watchlistSnackbarMessage by watchListViewModel.snackbarMessage.collectAsState()

    LaunchedEffect(favoriteSnackbarMessage) {
        favoriteSnackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoriteViewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(watchlistSnackbarMessage) {
        watchlistSnackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            watchListViewModel.clearSnackbarMessage()
        }
    }

    Log.d("MovieListScreen", "Movies Count: ${movies.size}")
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("Navigate up")
                    ) {
                        @Suppress("DEPRECATION")
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate up",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Movie List",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }


            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("Movie LazyColumn")
            ) {
                items(movies.chunked(2)) { moviePair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        moviePair.forEach { movie ->
                            MovieCoverImageList(
                                movie = movie,
                                onMovieClick = onMovieClick,
                                imageUrl = "${
                                    K.BASE_IMAGE_URL.replace(
                                        "w500",
                                        imageSize
                                    )
                                }${movie.posterPath}",
                                modifier = Modifier.weight(1f),
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
        CustomSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}