package com.nafis.coilmovieapp.ui.home.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.ui.home.HomeViewModel
import com.nafis.coilmovieapp.ui.home.defaultPadding
import com.nafis.coilmovieapp.ui.home.itemSpacing
import com.nafis.coilmovieapp.utils.K

@Composable
fun TopContent(
    modifier: Modifier = Modifier,
    movie: Movie,
    onMovieClick: (id: Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    // State for storing loading time
    var startTime by remember { mutableStateOf(0L) }
    val imageUrl = "${K.BASE_IMAGE_URL}${movie.posterPath}"
    val existingLoadingTime = homeViewModel.getLoadingTime(movie.title, imageUrl)

    LaunchedEffect(imageUrl) {
        startTime = 0L
    }

    // Create listener for Coil
    val listener = if (existingLoadingTime == null) {
        object : ImageRequest.Listener {
            override fun onStart(request: ImageRequest) {
                Log.d("ImageURL", "Image URL: $imageUrl")
                startTime = System.nanoTime()
                Log.d("LoadingTime", "Start loading image for ${movie.title}")
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                if (startTime == 0L) {
                    Log.w("TopContent", "Loading time skipped (cached image?)")
                    return
                }
                val endTime = System.nanoTime()
                val loadingTime = (endTime - startTime) / 1_000_000

                val runtime = Runtime.getRuntime()
                val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                Log.d("LoadingTime", "Coil loading time for ${movie.title}: $loadingTime ms")
                Log.d("MemoryUsage", "Memori setelah loading ${movie.title}: ${usedMemInMB} MB")
                homeViewModel.addLoadingTime(movie.title, imageUrl, loadingTime)
            }

            override fun onError(request: ImageRequest, result: ErrorResult) {
                if (startTime == 0L) {
                    Log.w("TopContent", "Error but startTime not set")
                    return
                }
                val endTime = System.nanoTime()
                val loadingTime = (endTime - startTime) / 1_000_000
                Log.d("LoadingTime", "Coil loading failed for ${movie.title}: $loadingTime ms")
                homeViewModel.addLoadingTime(movie.title, imageUrl, loadingTime)
            }
        }
    } else {
        null
    }

    // Using Coil Image Loader to load the image
    val imgRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .listener(listener)
        .build()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.id) }
    ) {
        AsyncImage(
            model = imgRequest,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .testTag("topContentImage"),
            contentScale = ContentScale.Crop
        )
        MovieDetail(
            rating = movie.voteAverage,
            title = movie.title,
            genre = movie.genreIds,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp)
        )
    }
}

@Composable
fun MovieDetail(
    modifier: Modifier = Modifier,
    rating: Double,
    title: String,
    genre: List<String>
) {
    Column(
        modifier = modifier.padding(defaultPadding)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color.Yellow
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = rating.toString())
        }
        Spacer(modifier = Modifier.height(itemSpacing))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(itemSpacing))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            genre.forEachIndexed { index, genreText ->
                if (index != 0) {
                    VerticalDivider(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = genreText,
                    modifier = Modifier.padding(6.dp).weight(1f),
                    maxLines = 1,
                )
                if (index != genre.lastIndex) {
                    VerticalDivider(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}