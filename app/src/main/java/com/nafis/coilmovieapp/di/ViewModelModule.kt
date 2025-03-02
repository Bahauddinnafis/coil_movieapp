package com.nafis.coilmovieapp.di

import com.nafis.coilmovieapp.movie.domain.repository.MovieRepository
import com.nafis.coilmovieapp.ui.home.HomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideHomeViewModel(repository: MovieRepository): HomeViewModel {
        return HomeViewModel(repository)
    }
}