package com.nafis.coilmovieapp.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.ui.CustomSnackbar
import com.nafis.coilmovieapp.ui.favorite.FavoriteViewModel
import com.nafis.coilmovieapp.ui.home.itemSpacing
import com.nafis.coilmovieapp.ui.watchlist.WatchListViewModel
import com.nafis.coilmovieapp.utils.K

@Composable
fun BodyContent(
    modifier: Modifier = Modifier,
    discoverMovies: List<Movie>,
    trendingMovies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onDiscoverMoviesClick: () -> Unit,
    onTrendingMoviesClick: () -> Unit,
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(itemSpacing),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Discover Movies",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("Discover Movies Text")
                        )
                        IconButton(
                            onClick = onDiscoverMoviesClick,
                            modifier = Modifier.testTag("More discover movies Icon")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "More discover movies",
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .testTag("Discover LazyRow"),
                    ) {
                        items(discoverMovies) { movie ->
                            MovieCoverImage(
                                movie = movie,
                                onMovieClick = onMovieClick,
                                imageUrl = "${
                                    K.BASE_IMAGE_URL.replace(
                                        "w500",
                                        "w154"
                                    )
                                }${movie.posterPath}",
                                testTag = "DiscoverMovie",
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(itemSpacing),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Trending now",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("Trending now Text")
                        )
                        IconButton(
                            onClick = onTrendingMoviesClick,
                            modifier = Modifier.testTag("Trending now Icon")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Trending now"
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .testTag("Trending LazyRow")
                    ) {
                        items(trendingMovies) { movie ->
                            MovieCoverImage(
                                movie = movie,
                                onMovieClick = onMovieClick,
                                imageUrl = "${
                                    K.BASE_IMAGE_URL.replace(
                                        "w500",
                                        "w185"
                                    )
                                }${movie.posterPath}",
                                testTag = "TrendingMovie",
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