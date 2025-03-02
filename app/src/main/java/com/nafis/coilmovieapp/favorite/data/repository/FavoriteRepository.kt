package com.nafis.coilmovieapp.favorite.data.repository

import com.nafis.coilmovieapp.favorite.data.local.FavoriteDao
import com.nafis.coilmovieapp.favorite.data.local.FavoriteListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun addToFavorite(movie: FavoriteListItem) {
        favoriteDao.addToFavorite(movie)
    }

    fun getFavorite(): Flow<List<FavoriteListItem>> {
        return favoriteDao.getFavoriteList()
    }

    suspend fun removeFromFavorite(id: Int) {
        favoriteDao.removeFromFavorite(id)
    }
}