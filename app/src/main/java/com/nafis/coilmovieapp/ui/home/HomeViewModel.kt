package com.nafis.coilmovieapp.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie.domain.repository.MovieRepository
import com.nafis.coilmovieapp.utils.collectAndHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {
    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    // State untuk menyimpan loading time
    private val _loadingTimes = mutableStateMapOf<String, Long>()
    val loadingTimes: Map<String, Long> get() = _loadingTimes

    private val _loadedImageKeys = mutableSetOf<String>()

    // Fungsi untuk menambahkan loading time
    fun addLoadingTime(movieTitle: String, imageSize: String, time: Long) {
        val key = "$movieTitle-$imageSize"
        // Hanya tambahkan loading time jika belum pernah dicatat sebelumnya
        // Dan waktu loading lebih dari 0
        if (!_loadedImageKeys.contains(key) && time > 0) {
            _loadingTimes[key] = time
            _loadedImageKeys.add(key)
        }
    }

    // Fungsi untuk menghapus loading time
    fun clearLoadingTimes() {
        _loadingTimes.clear()
        _loadedImageKeys.clear()
    }

    fun getLoadingTime(movieTitle: String, imageSize: String): Long? {
        val key = "$movieTitle-$imageSize"
        return _loadingTimes[key]
    }

    fun setHomeState(newState: HomeState) {
        _homeState.value = newState
    }

    init {
        fetchDiscoverMovie()
        fetchTrendingMovie()
    }

    fun fetchDiscoverMovie() = viewModelScope.launch {
        repository.fetchDiscoverMovie().collectAndHandle(
            onError = { error ->
                Log.e("HomeViewModel", "Error fetching discover movies: ${error?.message}")
                _homeState.update {
                    it.copy(isLoading = false, error = error?.message)
                }
            },
            onLoading = {
                Log.d("HomeViewModel", "Loading discover movies...")
                _homeState.update {
                    it.copy(isLoading = true, error = null)
                }
            }
        ) { movie ->
            Log.d("HomeViewModel", "Discover movies loaded: ${movie.size} items")
            _homeState.update {
                it.copy(isLoading = false, error = null, discoverMovies = movie)
            }
        }
    }

    fun fetchTrendingMovie() = viewModelScope.launch {
        repository.fetchTrendingMovie().collectAndHandle(
            onError = { error ->
                Log.e("HomeViewModel", "Error fetching trending movies: ${error?.message}")
                _homeState.update {
                    it.copy(isLoading = false, error = error?.message)
                }
            },
            onLoading = {
                Log.d("HomeViewModel", "Loading trending movies...")
                _homeState.update {
                    it.copy(isLoading = true, error = null)
                }
            }
        ) { movie ->
            Log.d("HomeViewModel", "Trending movies loaded: ${movie.size} items")
            _homeState.update {
                it.copy(isLoading = false, error = null, trendingMovies = movie)
            }
        }
    }
}

data class HomeState(
    val discoverMovies: List<Movie> = emptyList(),
    val trendingMovies: List<Movie> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
)