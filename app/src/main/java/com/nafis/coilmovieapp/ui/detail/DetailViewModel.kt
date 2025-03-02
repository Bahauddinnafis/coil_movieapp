package com.nafis.coilmovieapp.ui.detail

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie_detail.domain.models.Cast
import com.nafis.coilmovieapp.movie_detail.domain.models.MovieDetail
import com.nafis.coilmovieapp.movie_detail.domain.repository.MovieDetailRepository
import com.nafis.coilmovieapp.utils.K
import com.nafis.coilmovieapp.utils.collectAndHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _detailState = MutableStateFlow(DetailState())
    val detailState = _detailState.asStateFlow()

    // State untuk menyimpan loading time
    private val _loadingTimes = mutableStateMapOf<String, Long>()
    val loadingTimes: Map<String, Long> get() = _loadingTimes

    fun initData() {
        fetchMovieDetailById()
        fetchMovie()
    }

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

    var id: Int = savedStateHandle.get<Int>(K.MOVIE_ID) ?: -1

    fun setDetailState(newState: DetailState) {
        _detailState.update {
            newState
        }
    }

    private fun fetchMovieDetailById() = viewModelScope.launch {
        if (id == 1) {
            _detailState.update {
                it.copy(isLoading = false, error = "Movie not found")
            }
        } else {
            repository.fetchMovieDetail(id).collectAndHandle(
                onError = { error ->
                    _detailState.update {
                        it.copy(isLoading = false, error = error?.message)
                    }
                },
                onLoading = {
                    _detailState.update {
                        it.copy(isLoading = true, error = null)
                    }
                }
            ) { movieDetail ->
                _detailState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        movieDetail = movieDetail,
                        castList = movieDetail.cast,
                        isInitialLoad = false
                    )
                }
            }
        }
    }

    fun fetchMovie() = viewModelScope.launch {
        repository.fetchMovie().collectAndHandle(
            onError = { error ->
                _detailState.update {
                    it.copy(isMovieLoading = false, error = error?.message)
                }
            },
            onLoading = {
                _detailState.update {
                    it.copy(isMovieLoading = true, error = null)
                }
            }
        ) { movies ->
            _detailState.update {
                it.copy(
                    isLoading = false,
                    error = null,
                    movies = movies
                )
            }
        }
    }
}

data class DetailState(
    val isInitialLoad: Boolean = true,
    val movieDetail: MovieDetail? = null,
    val movies: List<Movie> = emptyList(),
    val castList: List<Cast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isMovieLoading: Boolean = false,
)