package com.nafis.coilmovieapp.watchlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchListItem(
    @PrimaryKey
    val id: Int,

    val title: String,
    val posterPath: String,
)