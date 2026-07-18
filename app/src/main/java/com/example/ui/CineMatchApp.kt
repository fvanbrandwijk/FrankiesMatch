package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.api.GeminiMovieItem
import com.example.data.MovieRating
import com.example.ui.theme.AvatarBackground
import com.example.ui.theme.AvatarText
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoContainerBlush
import com.example.ui.theme.GeoIndicatorPill
import com.example.ui.theme.GeoOnIndicator
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurfaceWhite
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.RedAccent
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineMatchApp(viewModel: MovieViewModel) {
    var activeTab by remember { mutableStateOf(0) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    val ratedMovies by viewModel.ratedMovies.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()

    // State for rating dialog
    var ratingTargetMovie by remember { mutableStateOf<GeminiMovieItem?>(null) }
    var ratingTargetSaved by remember { mutableStateOf<MovieRating?>(null) }
    var selectedScore by remember { mutableFloatStateOf(8.0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "FrankieMatch Icon",
                            tint = GeoPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "FrankieMatch",
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            fontSize = 24.sp,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.weight(1f)
                        )
                        // Geometric Balance User Avatar Placeholder (User initials: FV)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AvatarBackground)
                        ) {
                            Text(
                                text = "FV",
                                color = AvatarText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GeoBackground,
                    titleContentColor = GeoTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = GeoContainerBlush,
                tonalElevation = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Zoeken") },
                    label = { Text("AI Zoeken", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GeoOnIndicator,
                        selectedTextColor = GeoTextPrimary,
                        indicatorColor = GeoIndicatorPill,
                        unselectedIconColor = GeoTextSecondary,
                        unselectedTextColor = GeoTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Watchlist") },
                    label = { Text("Watchlist", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GeoOnIndicator,
                        selectedTextColor = GeoTextPrimary,
                        indicatorColor = GeoIndicatorPill,
                        unselectedIconColor = GeoTextSecondary,
                        unselectedTextColor = GeoTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Beoordelingen") },
                    label = { Text("Ratings", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GeoOnIndicator,
                        selectedTextColor = GeoTextPrimary,
                        indicatorColor = GeoIndicatorPill,
                        unselectedIconColor = GeoTextSecondary,
                        unselectedTextColor = GeoTextSecondary
                    )
                )
            }
        },
        containerColor = GeoBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GeoBackground)
        ) {
            when (activeTab) {
                0 -> SearchScreen(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    isSearching = isSearching,
                    results = searchResults,
                    error = searchError,
                    onSearch = { viewModel.performSearch() },
                    onAddToWatchlist = { viewModel.addToWatchlist(it) },
                    onRateMovie = { item ->
                        ratingTargetMovie = item
                        ratingTargetSaved = null
                        selectedScore = 8.0f
                    },
                    watchlist = watchlist,
                    ratedMovies = ratedMovies
                )
                1 -> WatchlistScreen(
                    watchlist = watchlist,
                    onRemove = { viewModel.removeRating(it) },
                    onRate = { item ->
                        ratingTargetSaved = item
                        ratingTargetMovie = null
                        selectedScore = 8.0f
                    }
                )
                2 -> RatingsScreen(
                    ratedMovies = ratedMovies,
                    onUpdateRating = { item ->
                        ratingTargetSaved = item
                        ratingTargetMovie = null
                        selectedScore = item.rating ?: 8.0f
                    },
                    onMoveToWatchlist = { viewModel.moveToWatchlist(it) },
                    onDelete = { viewModel.removeRating(it) }
                )
            }

            // Dialog for rating
            if (ratingTargetMovie != null || ratingTargetSaved != null) {
                val title = ratingTargetMovie?.title ?: ratingTargetSaved?.title ?: ""
                AlertDialog(
                    onDismissRequest = {
                        ratingTargetMovie = null
                        ratingTargetSaved = null
                    },
                    containerColor = GeoSurfaceWhite,
                    shape = RoundedCornerShape(28.dp),
                    title = {
                        Text(
                            text = "Geef een cijfer",
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                color = GeoPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Rating Badge
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(GeoIndicatorPill)
                            ) {
                                Text(
                                    text = String.format("%.1f", selectedScore),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoOnIndicator
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Slider
                            Slider(
                                value = selectedScore,
                                onValueChange = { selectedScore = (it * 2).roundToInt() / 2f },
                                valueRange = 1f..10f,
                                steps = 17, // 17 steps between 1.0 and 10.0 for 0.5 steps
                                colors = SliderDefaults.colors(
                                    thumbColor = GeoPrimary,
                                    activeTrackColor = GeoPrimary,
                                    inactiveTrackColor = GeoContainerBlush
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("1.0", color = GeoTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Slecht", color = GeoTextSecondary, fontSize = 12.sp)
                                Text("Geweldig!", color = GeoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("10.0", color = GeoTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                ratingTargetMovie?.let {
                                    viewModel.rateMovie(it, selectedScore)
                                }
                                ratingTargetSaved?.let {
                                    viewModel.updateSavedRating(it, selectedScore)
                                }
                                ratingTargetMovie = null
                                ratingTargetSaved = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Opslaan", color = GeoSurfaceWhite, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                ratingTargetMovie = null
                                ratingTargetSaved = null
                            }
                        ) {
                            Text("Annuleren", color = GeoTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    results: List<GeminiMovieItem>,
    error: String?,
    onSearch: () -> Unit,
    onAddToWatchlist: (GeminiMovieItem) -> Unit,
    onRateMovie: (GeminiMovieItem) -> Unit,
    watchlist: List<MovieRating>,
    ratedMovies: List<MovieRating>
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcoming header (if no search done yet)
        if (results.isEmpty() && !isSearching) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vind jouw perfecte film of serie",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FrankieMatch AI zoekt de mooiste titels, vindt streaming platforms en helpt je ratings bij te houden.",
                    fontSize = 14.sp,
                    color = GeoTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Search text field (Geometric rounded-full container styling)
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Bijv: Notting Hill, Omar Sy, of scifi...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Zoeken", tint = GeoPrimary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Wissen",
                            tint = GeoTextSecondary
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GeoTextPrimary,
                unfocusedTextColor = GeoTextPrimary,
                focusedContainerColor = GeoContainerBlush,
                unfocusedContainerColor = GeoContainerBlush,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedPlaceholderColor = GeoTextSecondary.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = GeoTextSecondary.copy(alpha = 0.6f)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch()
                keyboardController?.hide()
            })
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onSearch()
                keyboardController?.hide()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
            enabled = query.isNotBlank() && !isSearching
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GeoSurfaceWhite)
                Text(
                    text = if (isSearching) "AI Zoekt..." else "AI Zoeken & Aanbevelen",
                    fontWeight = FontWeight.Bold,
                    color = GeoSurfaceWhite
                )
            }
        }

        // Quick filter pills for "Geometric Balance" look
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(GeoSurfaceWhite, RoundedCornerShape(20.dp))
                    .border(BorderStroke(1.dp, GeoContainerBlush), RoundedCornerShape(20.dp))
                    .clickable { onQueryChange("Nederlandse films") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Nederlands", fontSize = 12.sp, color = GeoTextPrimary, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .background(GeoPrimary, RoundedCornerShape(20.dp))
                    .clickable { onQueryChange("Romantic comedy like Notting Hill") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("English", fontSize = 12.sp, color = GeoSurfaceWhite, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .background(GeoSurfaceWhite, RoundedCornerShape(20.dp))
                    .border(BorderStroke(1.dp, GeoContainerBlush), RoundedCornerShape(20.dp))
                    .clickable { onQueryChange("Beste films op Netflix") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Platforms", fontSize = 12.sp, color = GeoTextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State displays
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GeoPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("FrankieMatch AI zoekt films & series...", color = GeoTextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = RedAccent, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(error, color = RedAccent, textAlign = TextAlign.Center)
                }
            }
        } else if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = GeoContainerBlush,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Geen actieve zoekopdracht",
                        color = GeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Typ bijvoorbeeld 'Omar Sy' om al zijn films te vinden, of 'Nothing Hill' voor mooie romantische films.",
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { movie ->
                    val isSavedInWatchlist = watchlist.any { it.title.equals(movie.title, ignoreCase = true) }
                    val userScore = ratedMovies.find { it.title.equals(movie.title, ignoreCase = true) }?.rating

                    MovieResultCard(
                        movie = movie,
                        isSavedInWatchlist = isSavedInWatchlist,
                        userScore = userScore,
                        onAddToWatchlist = { onAddToWatchlist(movie) },
                        onRateMovie = { onRateMovie(movie) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieResultCard(
    movie: GeminiMovieItem,
    isSavedInWatchlist: Boolean,
    userScore: Float?,
    onAddToWatchlist: () -> Unit,
    onRateMovie: () -> Unit
) {
    val context = LocalContext.current
    // Media cards with custom white background, rounded-3xl corners, and border matching the blush color
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceWhite),
        border = BorderStroke(1.dp, GeoContainerBlush),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image with rounded-2xl interior container and geometric gradient fallback with large initial
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GeoTextPrimary,
                                GeoTextSecondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Artistically styled background initial for fallback
                val firstChar = if (movie.title.isNotEmpty()) movie.title.first().toString() else "M"
                Text(
                    text = firstChar,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.15f),
                    textAlign = TextAlign.Center
                )

                val cleanQuery = movie.imageSearchQuery.trim()
                    .replace(" ", ",")
                    .replace("-", ",")
                    .replace("_", ",")
                AsyncImage(
                    model = "https://images.unsplash.com/featured/800x450/?$cleanQuery,cinema,movie",
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Elegant translucent text banner at the bottom of image container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tags for Type and Year at the top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Type Badge
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (movie.type.lowercase().contains("series")) Icons.Default.Tv else Icons.Default.Movie,
                            contentDescription = null,
                            tint = GeoIndicatorPill,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (movie.type.lowercase().contains("series")) "Serie" else "Film",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Year Badge
                    Text(
                        text = movie.year,
                        color = GeoOnIndicator,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(GeoIndicatorPill, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Info Section
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Genres
                Text(
                    text = movie.genres,
                    color = GeoPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Description
                Text(
                    text = movie.description,
                    color = GeoTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Platforms
                if (movie.platforms.isNotBlank()) {
                    Text(
                        text = "Te zien op:",
                        color = GeoTextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        movie.platforms.split(",").forEach { platform ->
                            val cleanPlatform = platform.trim()
                            if (cleanPlatform.isNotEmpty()) {
                                Text(
                                    text = cleanPlatform,
                                    color = GeoTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(GeoContainerBlush, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Action Buttons (Geometric full-rounded pills style)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Watchlist button
                    Button(
                        onClick = onAddToWatchlist,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSavedInWatchlist) GeoContainerBlush else GeoPrimary.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = if (isSavedInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSavedInWatchlist) "Watchlist" else "Opslaan",
                            color = GeoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Trailer button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(movie.title + " official trailer")}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GeoSurfaceWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Trailer",
                            color = GeoSurfaceWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Rating button
                    Button(
                        onClick = onRateMovie,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (userScore != null) GeoIndicatorPill else GeoContainerBlush
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = if (userScore != null) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = GeoOnIndicator,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (userScore != null) "Cijfer: $userScore" else "Cijfer",
                            color = GeoOnIndicator,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WatchlistScreen(
    watchlist: List<MovieRating>,
    onRemove: (MovieRating) -> Unit,
    onRate: (MovieRating) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mijn Watchlist",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GeoPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (watchlist.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = GeoContainerBlush,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Je watchlist is leeg",
                        color = GeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gebruik de AI Zoeken tab om mooie films en series te ontdekken en ze direct hier op te slaan.",
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(watchlist) { item ->
                    SavedMovieCard(
                        item = item,
                        onRemove = { onRemove(item) },
                        onPrimaryAction = { onRate(item) },
                        primaryActionLabel = "Beoordeel",
                        primaryActionIcon = Icons.Default.Star
                    )
                }
            }
        }
    }
}

@Composable
fun RatingsScreen(
    ratedMovies: List<MovieRating>,
    onUpdateRating: (MovieRating) -> Unit,
    onMoveToWatchlist: (MovieRating) -> Unit,
    onDelete: (MovieRating) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mijn Beoordelingen",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GeoPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (ratedMovies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = GeoContainerBlush,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Geen beoordelingen gevonden",
                        color = GeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Geef films en series die je hebt gezien een cijfer om hier je eigen bibliotheek op te bouwen.",
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(ratedMovies) { item ->
                    SavedMovieCard(
                        item = item,
                        onRemove = { onDelete(item) },
                        onPrimaryAction = { onUpdateRating(item) },
                        primaryActionLabel = "Wijzig",
                        primaryActionIcon = Icons.Default.Star,
                        extraAction = {
                            IconButton(onClick = { onMoveToWatchlist(item) }) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Zet terug op watchlist",
                                    tint = GeoPrimary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavedMovieCard(
    item: MovieRating,
    onRemove: () -> Unit,
    onPrimaryAction: () -> Unit,
    primaryActionLabel: String,
    primaryActionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    extraAction: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceWhite),
        border = BorderStroke(1.dp, GeoContainerBlush)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image query backdrop loaded via Coil with gradient and initial fallback
                val cleanQuery = item.imageSearchQuery.trim()
                    .replace(" ", ",")
                    .replace("-", ",")
                    .replace("_", ",")
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GeoTextPrimary,
                                    GeoTextSecondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val firstChar = if (item.title.isNotEmpty()) item.title.first().toString() else "M"
                    Text(
                        text = firstChar,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White.copy(alpha = 0.15f),
                        textAlign = TextAlign.Center
                    )

                    AsyncImage(
                        model = "https://images.unsplash.com/featured/300x300/?$cleanQuery,cinema",
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Mid detail area
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.year} • ${if (item.type.lowercase().contains("series")) "Serie" else "Film"}",
                        color = GeoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.genres,
                        color = GeoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // If rated, show rating badge
                if (item.rating != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GeoIndicatorPill)
                    ) {
                        Text(
                            text = String.format("%.1f", item.rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoOnIndicator
                        )
                    }
                }

                // Delete action button
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Verwijderen",
                        tint = RedAccent
                    )
                }
            }

            // Description and platforms
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                Text(
                    text = item.description,
                    color = GeoTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (item.platforms.isNotBlank()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        item.platforms.split(",").forEach { platform ->
                            val cleanPlatform = platform.trim()
                            if (cleanPlatform.isNotEmpty()) {
                                Text(
                                    text = cleanPlatform,
                                    color = GeoTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(GeoContainerBlush, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (extraAction != null) {
                        extraAction()
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(item.title + " official trailer")}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp).padding(end = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GeoSurfaceWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Trailer",
                            color = GeoSurfaceWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onPrimaryAction,
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = primaryActionIcon,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = primaryActionLabel,
                            color = GeoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
