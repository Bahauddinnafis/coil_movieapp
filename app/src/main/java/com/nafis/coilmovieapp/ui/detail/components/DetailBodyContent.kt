package com.nafis.coilmovieapp.ui.detail.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nafis.coilmovieapp.favorite.data.local.FavoriteListItem
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie_detail.domain.models.MovieDetail
import com.nafis.coilmovieapp.movie_detail.domain.models.Review
import com.nafis.coilmovieapp.ui.CustomSnackbar
import com.nafis.coilmovieapp.ui.detail.components.ActorItem
import com.nafis.coilmovieapp.ui.favorite.FavoriteViewModel
import com.nafis.coilmovieapp.ui.home.components.MovieCard
import com.nafis.coilmovieapp.ui.home.components.MovieCoverImage
import com.nafis.coilmovieapp.ui.home.defaultPadding
import com.nafis.coilmovieapp.ui.home.itemSpacing
import com.nafis.coilmovieapp.ui.watchlist.WatchListViewModel
import com.nafis.coilmovieapp.utils.K
import com.nafis.coilmovieapp.watchlist.data.local.WatchListItem

@Composable
fun DetailBodyContent(
    modifier: Modifier = Modifier,
    movieDetail: MovieDetail,
    movies: List<Movie>,
    isMovieLoading: Boolean,
    fetchMovies: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onActorClick: (Int) -> Unit,
    onDiscoverActor: () -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    watchListViewModel: WatchListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
        LazyColumn(modifier = modifier.testTag("DetailBodyContent")) {
            // Item 1: Konten Utama (Genre, Title, Overview, Action Buttons, Cast & Crew, Movie Info, Reviews)
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(defaultPadding)
                    ) {
                        // Genre dan Runtime
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                movieDetail.genreIds.forEachIndexed { index, genreText ->
                                    Text(
                                        text = genreText,
                                        modifier = Modifier.padding(6.dp),
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (index != movieDetail.genreIds.lastIndex) {
                                        Text(
                                            text = " \u2002", // UI code for dot (.)
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                }
                            }
                            Text(
                                text = movieDetail.runTime,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Title
                        Text(
                            text = movieDetail.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Overview
                        Text(
                            text = movieDetail.overview,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Action Buttons
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ActionIcon.entries.forEachIndexed { index, actionIcon ->
                                ActionIconBtn(
                                    icon = actionIcon.icon,
                                    contentDescription = actionIcon.contentDescription,
                                    bgColor = if (index == ActionIcon.entries.lastIndex)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else Color.Black.copy(.5f),
                                    onClick = {
                                        when (actionIcon) {
                                            ActionIcon.Favorite -> {
                                                favoriteViewModel.addToFavorite(
                                                    FavoriteListItem(
                                                        id = movieDetail.id,
                                                        title = movieDetail.title,
                                                        posterPath = movieDetail.posterPath
                                                    )
                                                )
                                            }

                                            ActionIcon.Watchlist -> {
                                                watchListViewModel.addToWatchlist(
                                                    WatchListItem(
                                                        id = movieDetail.id,
                                                        title = movieDetail.title,
                                                        posterPath = movieDetail.posterPath
                                                    )
                                                )
                                            }

                                            ActionIcon.Share -> {
                                                shareMovieLink(
                                                    context,
                                                    movieDetail.id,
                                                    movieDetail.posterPath
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Cast & Crew
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = itemSpacing),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cast & Crew",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            IconButton(onClick = onDiscoverActor) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "More Cast & Crew"
                                )
                            }
                        }
                        LazyRow(
                            modifier = Modifier.testTag("ActorLazyRow")
                        ) {
                            items(movieDetail.cast) { cast ->
                                ActorItem(
                                    cast = cast,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onActorClick(cast.id) },
                                    imageUrl = "${
                                        K.BASE_IMAGE_URL.replace(
                                            "w500",
                                            "w92"
                                        )
                                    }${cast.profilePath}"
                                )
                                Spacer(modifier = Modifier.width(defaultPadding))
                            }
                        }
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Movie Info (Language, Production Countries)
                        MovieInfoItem(
                            infoItem = movieDetail.language,
                            title = "Spoken language"
                        )
                        Spacer(modifier = Modifier.height(itemSpacing))
                        MovieInfoItem(
                            infoItem = movieDetail.productionCountry,
                            title = "Production countries"
                        )
                        Spacer(modifier = Modifier.height(itemSpacing))

                        // Reviews
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(itemSpacing))
                        Review(reviews = movieDetail.reviews)
                    }
                }
            }

            // Item 2: More Like This
            item {
                MoreLikeThis(
                    fetchMovies = fetchMovies,
                    isMovieLoading = isMovieLoading,
                    movies = movies,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.testTag("MoreLikeThis")
                )
            }
        }
        CustomSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MoreLikeThis(
    modifier: Modifier = Modifier,
    fetchMovies: () -> Unit,
    isMovieLoading: Boolean,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    watchListViewModel: WatchListViewModel = hiltViewModel()
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

    LaunchedEffect(key1 = true) {
        fetchMovies
    }
    Column(modifier) {
        Text(
            text = "More like this",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        LazyRow(
            modifier = Modifier.testTag("MoreLikeThisLazyRow")
        ) {
            item {
                AnimatedVisibility(visible = isMovieLoading) {
                    CircularProgressIndicator()
                }
            }
            items(movies) {movie ->
                MovieCoverImage(
                    movie = movie,
                    onMovieClick = onMovieClick,
                    imageUrl = "${K.BASE_IMAGE_URL}${movie.posterPath}",
                    snackbarHostState = snackbarHostState
                )
            }
        }
        CustomSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private enum class ActionIcon(val icon: ImageVector, val contentDescription: String) {
    Favorite(icon = Icons.Default.Favorite, contentDescription = "Favorite_3"),
    Watchlist(icon = Icons.Default.AddToQueue, contentDescription = "Watchlist_3"),
    Share(icon = Icons.Default.Share, contentDescription = "Share"),
}

private fun generateMovieLink(movieId: Int, posterPath: String): String {
    val baseUrl = K.BASE_URL
    return "https://www.themoviedb.org/movie/$movieId"
}

private fun shareMovieLink(context: Context, movieId: Int, posterPath: String) {
    val link = generateMovieLink(movieId, posterPath)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Check out this movie poster: $link")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

@Composable
fun ActionIconBtn(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    bgColor: Color = Color.Black.copy(8f),
    onClick: () -> Unit
) {
    MovieCard(
        shapes = CircleShape,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() },
        bgColor = bgColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun MovieInfoItem(infoItem: List<String>, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        infoItem.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun Review(
    modifier: Modifier = Modifier,
    reviews: List<Review>
) {
    val (viewMore, setViewMore) = remember {
        mutableStateOf(false)
    }
    // show only three reviews or less by default
    val defaultReview = if (reviews.size > 3) reviews.take(3) else reviews
    // show more when user need more review
    val movieReviews = if (viewMore) reviews else defaultReview
    val btnText = if (viewMore) "Collapse" else "More..."
    Column {
        movieReviews.forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.height(itemSpacing))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(itemSpacing))
        }
        TextButton(onClick = { setViewMore(!viewMore) }) {
            Text(text = btnText)
        }
    }
}