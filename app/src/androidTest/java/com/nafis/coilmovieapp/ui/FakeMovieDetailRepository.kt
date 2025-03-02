package com.nafis.coilmovieapp.ui

import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie_detail.domain.models.MovieDetail
import com.nafis.coilmovieapp.movie_detail.domain.repository.MovieDetailRepository
import com.nafis.coilmovieapp.utils.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieDetailRepository : MovieDetailRepository {

    private val fakeMovieDetail = MovieDetail(
        backdropPath = "justo",
        genreIds = listOf(),
        id = 1,
        originalLanguage = "expetendis",
        originalTitle = "Movie 1",
        overview = "autem",
        popularity = 4.5,
        posterPath = "cum",
        releaseDate = "aptent",
        title = "Movie 1",
        voteAverage = 6.7,
        voteCount = 3078,
        video = false,
        cast = listOf(),
        language = listOf(),
        productionCountry = listOf(),
        reviews = listOf(),
        runTime = "mazim"
    )

    override fun fetchMovieDetail(movieId: Int): Flow<Response<MovieDetail>> = flow {
        emit(Response.Loading())
        delay(500)
        if (movieId == fakeMovieDetail.id) {
            emit(Response.Success(fakeMovieDetail))
        } else {
            emit(Response.Error(Exception("Movie not found")))
        }
    }

    override fun fetchMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        delay(500)
        emit(Response.Success(emptyList()))
    }
}