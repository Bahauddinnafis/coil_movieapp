package com.nafis.coilmovieapp.ui.detail.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nafis.coilmovieapp.movie_detail.domain.models.Cast
import com.nafis.coilmovieapp.ui.detail.DetailViewModel
import com.nafis.coilmovieapp.R

@Composable
fun ActorItemList(
    modifier: Modifier = Modifier,
    cast: Cast,
    imageUrl: String,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    Log.d("ActorItemList", "Loading image from URL: $imageUrl")

    // State untuk menyimpan waktu loading
    var startTime by remember { mutableStateOf(0L) }
    val existingLoadingTime = detailViewModel.getLoadingTime(cast.name, imageUrl)

    LaunchedEffect(imageUrl) {
        startTime = 0L
    }

    // Membuat listener untuk Coil
    val listener = if (existingLoadingTime == null) {
        object : ImageRequest.Listener {
            override fun onStart(request: ImageRequest) {
                Log.d("ImageURL", "Image URL: $imageUrl")
                startTime = System.nanoTime()
                Log.d("LoadingTime", "Start loading image for ${cast.name}")
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                if (startTime == 0L) {
                    Log.w("ActorItemList", "Loading time skipped (cached image?)")
                    return
                }
                val endTime = System.nanoTime()
                val loadingTime = (endTime - startTime) / 1_000_000

                val runtime = Runtime.getRuntime()
                val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                Log.d("LoadingTime", "Coil loading time for ${cast.name}: $loadingTime ms")
                Log.d("MemoryUsage", "Memori setelah loading ${cast.firstName}: ${usedMemInMB} MB")
                detailViewModel.addLoadingTime(cast.name, imageUrl, loadingTime)
            }

            override fun onError(request: ImageRequest, result: ErrorResult) {
                if (startTime == 0L) {
                    Log.w("ActorItemList", "Error but startTime not set")
                    return
                }
                val endTime = System.nanoTime()
                val loadingTime = (endTime - startTime) / 1_000_000
                Log.d("LoadingTime", "Coil loading failed for ${cast.name}: $loadingTime ms")
                detailViewModel.addLoadingTime(cast.name, imageUrl, loadingTime)
            }
        }
    } else {
        null
    }

    val imgRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .listener(listener)
        .build()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imgRequest,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.baseline_person_24)
        )
        // Gender Role (Actor / Actress)
        Text(text = cast.genderRole, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = cast.firstName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = cast.lastName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

    }
}