package com.nafis.coilmovieapp.movie.data.repository_impl

import com.nafis.coilmovieapp.common.data.ApiMapper
import com.nafis.coilmovieapp.movie.data.remote.api.MovieApiService
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie.domain.repository.MovieRepository
import com.nafis.coilmovieapp.movie.data.remote.models.MovieDto
import com.nafis.coilmovieapp.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.apply

class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val apiMapper: ApiMapper<List<Movie>, MovieDto>
): MovieRepository {
    override fun fetchDiscoverMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchDiscoverMovie()
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))
        }
    }.catch { e ->
        emit(Response.Error(e))
    }

    override fun fetchTrendingMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchTrendingMovie()
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))
        }
    }.catch { e ->
        emit(Response.Error(e))
    }
}