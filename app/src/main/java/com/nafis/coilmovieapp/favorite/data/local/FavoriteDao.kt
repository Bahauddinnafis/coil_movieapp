package com.nafis.coilmovieapp.favorite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addToFavorite(item: FavoriteListItem)

    @Query("SELECT * FROM favorite")
    fun getFavoriteList(): Flow<List<FavoriteListItem>>

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun removeFromFavorite(id: Int)
}