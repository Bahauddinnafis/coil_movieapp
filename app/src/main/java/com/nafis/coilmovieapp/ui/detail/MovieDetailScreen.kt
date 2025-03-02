package com.nafis.coilmovieapp.ui.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nafis.coilmovieapp.ui.components.LoadingView
import com.nafis.coilmovieapp.ui.detail.components.DetailBodyContent
import com.nafis.coilmovieapp.ui.detail.components.DetailTopContent

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    movieDetailViewModel: DetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onActorClick: (Int) -> Unit,
    onDiscoverActor: () -> Unit
) {
    val state = movieDetailViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state.value.isInitialLoad) {
            movieDetailViewModel.initData()
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            state.value.error != null,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Text(
                state.value.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                maxLines = 2,
            )
        }
        AnimatedVisibility(visible = !state.value.isLoading && state.value.error == null) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxHeight = maxHeight
                val topItemHeight = boxHeight * .4f
                val bodyItemHeight = boxHeight * .6f
                state.value.movieDetail?.let { movieDetail ->
                    DetailTopContent(
                        movieDetail = movieDetail,
                        modifier = Modifier
                            .height(topItemHeight)
                            .align(Alignment.TopCenter)
                    )
                    DetailBodyContent(
                        movieDetail = movieDetail,
                        movies = state.value.movies,
                        isMovieLoading = state.value.isMovieLoading,
                        fetchMovies = movieDetailViewModel::fetchMovie,
                        onMovieClick = onMovieClick,
                        onActorClick = onActorClick,
                        onDiscoverActor = onDiscoverActor,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .height(bodyItemHeight),
                    )
                }
            }
        }
        IconButton(onClick = onNavigateUp, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
        }
    }
    LoadingView(isLoading = state.value.isLoading)
}