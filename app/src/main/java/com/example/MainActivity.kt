package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.MovieRepository
import com.example.ui.CineMatchApp
import com.example.ui.MovieViewModel
import com.example.ui.MovieViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = MovieRepository(database.movieRatingDao())

        // Initialize ViewModel using Factory
        val viewModelFactory = MovieViewModelFactory(application, repository)
        val viewModel: MovieViewModel = ViewModelProvider(this, viewModelFactory)[MovieViewModel::class.java]

        setContent {
            MyApplicationTheme {
                CineMatchApp(viewModel = viewModel)
            }
        }
    }
}
