package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieRatingDao {
    @Query("SELECT * FROM movie_ratings ORDER BY addedAt DESC")
    fun getAllRatings(): Flow<List<MovieRating>>

    @Query("SELECT * FROM movie_ratings WHERE isWatchlist = 1 ORDER BY addedAt DESC")
    fun getWatchlist(): Flow<List<MovieRating>>

    @Query("SELECT * FROM movie_ratings WHERE isWatchlist = 0 AND rating IS NOT NULL ORDER BY addedAt DESC")
    fun getRatedMovies(): Flow<List<MovieRating>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(movieRating: MovieRating): Long

    @Update
    suspend fun updateRating(movieRating: MovieRating)

    @Delete
    suspend fun deleteRating(movieRating: MovieRating)

    @Query("SELECT * FROM movie_ratings WHERE title = :title LIMIT 1")
    suspend fun getRatingByTitle(title: String): MovieRating?
}
