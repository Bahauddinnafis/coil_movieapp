package com.nafis.coilmovieapp.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nafis.coilmovieapp.common.data.ApiMapper
import com.nafis.coilmovieapp.movie.data.mapper_impl.MovieApiMapperImpl
import com.nafis.coilmovieapp.movie.data.remote.models.MovieDto
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie_detail.data.mapper_impl.MovieDetailMapperImpl
import com.nafis.coilmovieapp.movie_detail.data.remote.api.MovieDetailApiService
import com.nafis.coilmovieapp.movie_detail.data.remote.repository_impl.MovieDetailRepositoryImpl
import com.nafis.coilmovieapp.movie_detail.domain.models.MovieDetail
import com.nafis.coilmovieapp.movie_detail.domain.repository.MovieDetailRepository
import com.nafis.coilmovieapp.utils.K
import com.nafis.coilmovieapp.movie_detail.data.remote.models.MovieDetailDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDetailModule {
    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    @Named("MovieDetailMapper") // Tambahkan @Named
    fun provideMovieDetailMapper(): ApiMapper<MovieDetail, MovieDetailDto> = MovieDetailMapperImpl()

    @Provides
    @Singleton
    @Named("MovieListMapper") // Tambahkan @Named
    fun provideMovieListMapper(): ApiMapper<List<Movie>, MovieDto> = MovieApiMapperImpl()

    @Provides
    @Singleton
    fun provideMovieDetailRepository(
        movieDetailApiService: MovieDetailApiService,
        @Named("MovieDetailMapper") detailMapper: ApiMapper<MovieDetail, MovieDetailDto>, // Gunakan @Named
        @Named("MovieListMapper") movieMapper: ApiMapper<List<Movie>, MovieDto> // Gunakan @Named
    ): MovieDetailRepository = MovieDetailRepositoryImpl(
        movieDetailApiService = movieDetailApiService,
        apiDetailMapper = detailMapper,
        apiMovieMapper = movieMapper
    )

    @Provides
    @Singleton
    fun provideMovieDetailApiService(): MovieDetailApiService {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(K.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MovieDetailApiService::class.java)
    }
}