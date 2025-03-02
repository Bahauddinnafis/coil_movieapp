package com.nafis.coilmovieapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafis.coilmovieapp.favorite.data.local.FavoriteListItem
import com.nafis.coilmovieapp.favorite.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: FavoriteRepository
): ViewModel() {

    private val _favorite = MutableStateFlow<List<FavoriteListItem>>(emptyList())
    val favorite: StateFlow<List<FavoriteListItem>> get() = _favorite

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> get() = _snackbarMessage

    init {
        loadFavorite()
    }

    fun addToFavorite(movie: FavoriteListItem) {
        viewModelScope.launch {
            val currentFavorites = _favorite.value
            if (currentFavorites.any { it.id == movie.id }) {
                _snackbarMessage.value = "Film sudah ada di favorite: ${movie.title}"
            } else {
                repository.addToFavorite(movie)
                _snackbarMessage.value = "Berhasil menambahkan ke favorite: ${movie.title}"
                loadFavorite()
            }
        }
    }

    fun loadFavorite() {
        viewModelScope.launch {
            repository.getFavorite().collect { list ->
                _favorite.value = list
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

}
