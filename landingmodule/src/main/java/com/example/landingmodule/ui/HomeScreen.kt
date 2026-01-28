package com.example.landingmodule.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.landingmodule.R
import com.example.landingmodule.ui.constants.StringConstants
import com.example.networkmodule.model.PokemonListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onPokemonClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Grayish background
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onSearch = {},
            active = false,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        ) {
        }


        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent()

            is HomeUiState.Success -> PokemonGrid(
                pokemonList = state.pokemonList,
                onPokemonClick = onPokemonClick,
                isLoadingMore = state.isLoadingMore,
                canLoadMore = viewModel.canLoadMore(),
                onLoadMore = {
                    coroutineScope.launch { viewModel.loadMorePokemon() }
                }
            )

            is HomeUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    isNetworkError = state.isNetworkError,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}

@Composable
private fun PokemonGrid(
    pokemonList: List<PokemonListItem>,
    onPokemonClick: (String) -> Unit,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit
) {
    if (pokemonList.isEmpty()) {
        EmptyState(message = stringResource(R.string.no_pokemon_found))
    } else {
        var visibleItems by remember { mutableStateOf(0) }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = pokemonList,
                key = { index, pokemon -> "${pokemon.getId()}-$index" }
            ) { index, pokemon ->
                // Vary card height per item to create a truly staggered layout
                val cardHeight = when (index % 4) {
                    0 -> 240.dp
                    1 -> 280.dp
                    2 -> 220.dp
                    else -> 260.dp
                }
                val delayMillis = index * 40L
                LaunchedEffect(pokemon.getId()) {
                    if (index >= visibleItems) {
                        delay(delayMillis.coerceAtMost(600L))
                        visibleItems = index + 1
                    }
                }

                AnimatedVisibility(
                    visible = index < visibleItems,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(durationMillis = 400, easing = EaseOutBack)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    ) + scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(durationMillis = 200)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                    ) {
                        PokemonCard(
                            pokemon = pokemon,
                            onClick = { onPokemonClick(pokemon.getId()) }
                        )
                    }
                }

                if (index == pokemonList.lastIndex && canLoadMore && !isLoadingMore) {
                    LaunchedEffect(Unit) {
                        onLoadMore()
                    }
                }
            }

            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: PokemonListItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // White card background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = "${StringConstants.POKEMON_IMAGE_BASE_URL}${pokemon.getId()}.png",
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_pokemon_placeholder),
                error = painterResource(R.drawable.ic_pokemon_error),
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${pokemon.getId().padStart(3, '0')}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ErrorContent(message: String, isNetworkError: Boolean, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isNetworkError) "No Internet Connection" else message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}
