package com.example.datamodule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.networkmodule.model.PokemonDetailResponse
import com.example.networkmodule.model.Stat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onBackClick: () -> Unit,
    viewModel: InfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pokemon_info_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Grayish background matching landing screen
                .systemBarsPadding()
        ) {
            when (val currentState = uiState) {
                is InfoUiState.Loading -> LoadingContent()
                is InfoUiState.Success -> PokemonDetailContent(
                    pokemon = currentState.pokemon,
                    scaffoldPadding = paddingValues
                )

                is InfoUiState.Error -> {
                    if (currentState.isNetworkError) {
                        currentState.message?.let {
                            NetworkErrorContent(
                                message = it,
                                onRetry = viewModel::refresh
                            )
                        }
                    } else {
                        currentState.message?.let {
                            SimpleErrorContent(
                                message = it,
                                onRetry = viewModel::refresh
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemon: PokemonDetailResponse,
    scaffoldPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "header_${pokemon.id}") {
            PokemonHeader(pokemon)
        }
        item(key = "stats_${pokemon.id}") {
            SectionCard(stringResource(R.string.stats_section_title)) {
                StatsList(pokemon.stats)
            }
        }
        item(key = "types_${pokemon.id}") {
            SectionCard(stringResource(R.string.types_section_title)) {
                TypesList(pokemon.types.map { it.type.name })
            }
        }
        item(key = "abilities_${pokemon.id}") {
            SectionCard(stringResource(R.string.abilities_section_title)) {
                AbilitiesList(pokemon.abilities.map { it.ability.name to it.isHidden })
            }
        }
        if (pokemon.forms.isNotEmpty()) {
            item(key = "forms_${pokemon.id}") {
                SectionCard(stringResource(R.string.forms_section_title)) {
                    FormsList(pokemon.forms.map { it.name })
                }
            }
        }
    }
}

@Composable
private fun PokemonHeader(pokemon: PokemonDetailResponse) = Card(
    modifier = Modifier.fillMaxWidth(),
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
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = pokemon.sprites.frontDefault
                ?: pokemon.sprites.other?.officialArtwork?.frontDefault,
            contentDescription = stringResource(R.string.pokemon_image_description, pokemon.name),
            placeholder = painterResource(R.drawable.ic_pokemon_placeholder),
            error = painterResource(R.drawable.ic_pokemon_error),
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = pokemon.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        Text(
            text = stringResource(
                R.string.pokemon_id_format,
                pokemon.id.toString().padStart(3, '0')
            ),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF666666)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(
                stringResource(R.string.height_label),
                "${pokemon.height / 10.0}${stringResource(R.string.height_unit)}"
            )
            InfoItem(
                stringResource(R.string.weight_label),
                "${pokemon.weight / 10.0}${stringResource(R.string.weight_unit)}"
            )
            pokemon.baseExperience?.let {
                InfoItem(
                    stringResource(R.string.base_exp_label),
                    it.toString()
                )
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        label,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF666666)
    )
    Text(
        value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF333333)
    )
}

@Composable
private fun StatsList(stats: List<Stat>) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.forEach { stat ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stat.stat.name.replaceFirstChar { it.uppercase() },
                    Modifier.weight(1f),
                    color = Color(0xFF333333)
                )
                Text(
                    stat.baseStat.toString(),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                LinearProgressIndicator(
                    progress = stat.baseStat / 255f,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

@Composable
private fun TypesList(types: List<String>) =
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        types.forEach { type ->
            AssistChip(onClick = {}, label = { Text(type.replaceFirstChar { it.uppercase() }) })
        }
    }

@Composable
private fun AbilitiesList(abilities: List<Pair<String, Boolean>>) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        abilities.forEach { (name, _) ->
            Text(
                text = name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
        }
    }

@Composable
private fun FormsList(forms: List<String>) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        forms.forEach {
            Text(
                it.replaceFirstChar { it.uppercase() },
                color = Color(0xFF333333)
            )
        }
    }

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) = Card(
    modifier = Modifier.fillMaxWidth(),
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
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        content()
    }
}
