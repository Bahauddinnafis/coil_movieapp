package com.nafis.coilmovieapp.movie.domain.repository

import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.utils.Response
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun fetchDiscoverMovie(): Flow<Response<List<Movie>>>
    fun fetchTrendingMovie(): Flow<Response<List<Movie>>>

}