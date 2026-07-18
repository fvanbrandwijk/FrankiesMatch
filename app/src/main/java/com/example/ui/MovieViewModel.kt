package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GeminiMovieItem
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.MovieRating
import com.example.data.MovieRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(
    application: Application,
    private val repository: MovieRepository
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeminiMovieItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError = _searchError.asStateFlow()

    // Database flows
    val allRatings: StateFlow<List<MovieRating>> = repository.allRatings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchlist: StateFlow<List<MovieRating>> = repository.watchlist.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val ratedMovies: StateFlow<List<MovieRating>> = repository.ratedMovies.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Moshi instance with Kotlin reflector
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            _searchResults.value = emptyList()

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _searchError.value = "Gemini API key is niet geconfigureerd. Voeg deze toe in de Secrets panel."
                    _isSearching.value = false
                    return@launch
                }

                val systemInstructionText = """
                    Je bent een deskundige film- en seriegids en aanbevelingsassistent.
                    De gebruiker zoekt naar filmtitels, series, acteurs of genres in het Nederlands of Engels.
                    Jouw taak is om de gezochte film/serie te tonen en/of een lijst met 5 tot 8 uitstekende bijpassende suggesties aan te bevelen.
                    
                    Corrigeer typefouten op een vriendelijke en slimme manier:
                    - Als ze zoeken op "Nothing Hill", geef dan "Notting Hill" en soortgelijke romantische comedy's (zoals About Time, Love Actually).
                    - Als ze zoeken op "Omar Shai" of "Omar Sy", geef dan films waarin hij meespeelt (zoals Intouchables, Lupin, Samba, Demain tout commence) én soortgelijke films.
                    
                    Geef voor elk item aan op welke streamingdiensten (platforms) ze te zien zijn in West-Europa (bijv. Netflix, Disney+, Prime Video, Apple TV, HBO Max, Videoland, Pathé Thuis, SkyShowtime, KPN). Als ze nergens te streamen zijn, schrijf dan "Niet streambaar" of "Huren/Kopen".
                    
                    Je MOET je antwoord formatteren als een JSON-array van objecten, zonder markdown-blokken (geen ```json code blocks, gewoon pure JSON-tekst).
                    
                    Elk object in de array MOET exact deze velden bevatten:
                    1. "title": De titel van de film of serie.
                    2. "type": Ofwel "movie" of "series".
                    3. "year": Jaar van uitgave (bijv. "1999" of "2021-heden").
                    4. "description": Een korte, verleidelijke omschrijving in de taal van de zoekopdracht (Nederlands of Engels). Maximaal 2 zinnen.
                    5. "genres": Comma-separated genres (bijv. "Romantiek, Komedie").
                    6. "platforms": Comma-separated platforms (bijv. "Netflix, Videoland").
                    7. "imageSearchQuery": Een korte Unsplash-zoekterm om een sfeerbeeld bij de film te laden (bijv. "london-bookstore" voor Notting Hill, of "paris-night" voor Intouchables, of algemene cinema termen passend bij de sfeer).
                """.trimIndent()

                val prompt = "Zoekopdracht van de gebruiker: \"$query\""

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.3f
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (responseText != null) {
                    // Strip any stray markdown if present
                    val cleanedText = responseText.trim()
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    val listType = Types.newParameterizedType(List::class.java, GeminiMovieItem::class.java)
                    val adapter = moshi.adapter<List<GeminiMovieItem>>(listType)
                    val parsedResults = adapter.fromJson(cleanedText)

                    if (parsedResults != null) {
                        _searchResults.value = parsedResults
                    } else {
                        _searchError.value = "Kon de resultaten niet correct verwerken."
                    }
                } else {
                    _searchError.value = "Geen antwoord ontvangen van FrankieMatch AI."
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Search error", e)
                _searchError.value = "Fout bij het zoeken: ${e.localizedMessage ?: e.message}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    // Database Actions
    fun addToWatchlist(item: GeminiMovieItem) {
        viewModelScope.launch {
            // Check if exists
            val existing = repository.getRatingByTitle(item.title)
            if (existing != null) {
                // Update to watchlist
                repository.updateRating(existing.copy(isWatchlist = true))
            } else {
                val newRating = MovieRating(
                    title = item.title,
                    type = item.type,
                    year = item.year,
                    description = item.description,
                    genres = item.genres,
                    platforms = item.platforms,
                    isWatchlist = true,
                    imageSearchQuery = item.imageSearchQuery
                )
                repository.insertRating(newRating)
            }
        }
    }

    fun rateMovie(item: GeminiMovieItem, score: Float) {
        viewModelScope.launch {
            val existing = repository.getRatingByTitle(item.title)
            if (existing != null) {
                repository.updateRating(existing.copy(rating = score, isWatchlist = false))
            } else {
                val newRating = MovieRating(
                    title = item.title,
                    type = item.type,
                    year = item.year,
                    description = item.description,
                    genres = item.genres,
                    platforms = item.platforms,
                    rating = score,
                    isWatchlist = false,
                    imageSearchQuery = item.imageSearchQuery
                )
                repository.insertRating(newRating)
            }
        }
    }

    fun updateSavedRating(movieRating: MovieRating, newScore: Float) {
        viewModelScope.launch {
            repository.updateRating(movieRating.copy(rating = newScore, isWatchlist = false))
        }
    }

    fun moveToWatchlist(movieRating: MovieRating) {
        viewModelScope.launch {
            repository.updateRating(movieRating.copy(isWatchlist = true, rating = null))
        }
    }

    fun removeRating(movieRating: MovieRating) {
        viewModelScope.launch {
            repository.deleteRating(movieRating)
        }
    }
}

class MovieViewModelFactory(
    private val application: Application,
    private val repository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
