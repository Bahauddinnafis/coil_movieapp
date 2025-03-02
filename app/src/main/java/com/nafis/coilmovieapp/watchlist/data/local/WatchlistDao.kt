package com.nafis.coilmovieapp.watchlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchListDao {
    @Insert
    suspend fun addToWatchList(item: WatchListItem)

    @Query("SELECT * FROM watchlist")
    fun getWatchList(): Flow<List<WatchListItem>>

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun removeFromWatchList(id: Int)
}