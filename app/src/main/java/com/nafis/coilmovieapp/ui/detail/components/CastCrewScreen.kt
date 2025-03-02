package com.nafis.coilmovieapp.ui.detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nafis.coilmovieapp.ui.components.LoadingView
import com.nafis.coilmovieapp.ui.detail.DetailViewModel
import com.nafis.coilmovieapp.ui.home.itemSpacing
import com.nafis.coilmovieapp.utils.K

@Composable
fun CastCrewScreen(
    modifier: Modifier = Modifier,
    movieDetailViewModel: DetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onActorClick: (id: Int) -> Unit
) {
    val state = movieDetailViewModel.detailState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            state.value.error != null,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Text(
                state.value.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                maxLines = 2
            )
        }
        AnimatedVisibility(
            visible = !state.value.isLoading && state.value.error == null
        ) {
            state.value.movieDetail?.let { movieDetail ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("CastCrewScreen")
                ) {
                    items(movieDetail.cast.chunked(2)) { castPair ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            castPair.forEach { cast ->
                                ActorItemList(
                                    cast = cast,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable{ onActorClick(cast.id) }
                                        .padding(top = itemSpacing),
                                    imageUrl = "${K.BASE_IMAGE_URL.replace("w500", "w92")}${cast.profilePath}"
                                )
                            }
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
    LoadingView(isLoading = state.value.isLoading)
}