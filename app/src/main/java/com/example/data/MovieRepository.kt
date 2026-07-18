package com.example.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieRatingDao: MovieRatingDao) {
    val allRatings: Flow<List<MovieRating>> = movieRatingDao.getAllRatings()
    val watchlist: Flow<List<MovieRating>> = movieRatingDao.getWatchlist()
    val ratedMovies: Flow<List<MovieRating>> = movieRatingDao.getRatedMovies()

    suspend fun insertRating(movieRating: MovieRating): Long {
        return movieRatingDao.insertRating(movieRating)
    }

    suspend fun updateRating(movieRating: MovieRating) {
        movieRatingDao.updateRating(movieRating)
    }

    suspend fun deleteRating(movieRating: MovieRating) {
        movieRatingDao.deleteRating(movieRating)
    }

    suspend fun getRatingByTitle(title: String): MovieRating? {
        return movieRatingDao.getRatingByTitle(title)
    }
}
