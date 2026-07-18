package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_ratings")
data class MovieRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "movie" or "series"
    val year: String,
    val description: String,
    val genres: String, // Comma-separated
    val platforms: String, // Comma-separated (e.g., "Netflix, Disney+")
    val rating: Float? = null, // User score (e.g., 8.5)
    val isWatchlist: Boolean = false,
    val imageSearchQuery: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
