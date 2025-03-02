package com.nafis.coilmovieapp.di

import android.content.Context
import com.nafis.coilmovieapp.ui.watchlist.WatchListViewModel
import com.nafis.coilmovieapp.watchlist.data.local.WatchlistDatabase
import com.nafis.coilmovieapp.watchlist.data.repository.WatchListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WatchListModule {

    @Provides
    @Singleton
    fun provideWatchListDatabase(@ApplicationContext context: Context): WatchlistDatabase {
        return WatchlistDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWatchListRepository(database: WatchlistDatabase): WatchListRepository {
        return WatchListRepository(database.watchlistDao())
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object WatchListViewModelModule {
    @Provides
    fun provideWatchListViewModel(repository: WatchListRepository): WatchListViewModel {
        return WatchListViewModel(repository)
    }
}