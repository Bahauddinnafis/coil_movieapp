package com.nafis.coilmovieapp.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafis.coilmovieapp.watchlist.data.local.WatchListItem
import com.nafis.coilmovieapp.watchlist.data.repository.WatchListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val repository: WatchListRepository,
) : ViewModel() {

    private val _watchlist = MutableStateFlow<List<WatchListItem>>(emptyList())
    val watchlist: StateFlow<List<WatchListItem>> get() = _watchlist

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> get() = _snackbarMessage

    init {
        loadWatchlist()
    }

    fun addToWatchlist(movie: WatchListItem) {
        viewModelScope.launch {
            val currentWatchlist = _watchlist.value
            if (currentWatchlist.any { it.id == movie.id }) {
                _snackbarMessage.value = "Film sudah ada di watchlist: ${movie.title}"
            } else {
                repository.addToWatchList(movie)
                _snackbarMessage.value = "Berhasil menambahkan ke watchlist: ${movie.title}"
                loadWatchlist()
            }
        }
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            repository.getWatchlist().collect { list ->
                _watchlist.value = list
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}