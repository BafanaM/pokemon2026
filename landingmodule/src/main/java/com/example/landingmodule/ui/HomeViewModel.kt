package com.example.landingmodule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkmodule.model.PokemonListItem
import com.example.networkmodule.usecase.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.core.Result
import com.example.landingmodule.ui.constants.StringConstants.KEYWORD_NETWORK
import com.example.landingmodule.ui.constants.StringConstants.KEYWORD_NO_INTERNET
import com.example.landingmodule.ui.constants.StringConstants.KEYWORD_TIMEOUT
import com.example.landingmodule.ui.constants.StringConstants.UNKNOWN_ERROR

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allPokemon: List<PokemonListItem> = emptyList()
    private var currentOffset = 0
    private val pageSize = 150
    private var hasMoreData = true
    private var isLoadingMore = false

    init {
        loadPokemonList()
        observeSearchQuery()
    }

    /**
     * Initial load of Pokemon list.
     */
    fun loadPokemonList() {
        viewModelScope.launch {
            currentOffset = 0
            hasMoreData = true
            allPokemon = emptyList()
            _uiState.value = HomeUiState.Loading

            getPokemonListUseCase(limit = pageSize, offset = currentOffset)
                .collectLatest { result: Result<List<PokemonListItem>> ->
                    handleResult(result, isInitialLoad = true)
                }
        }
    }

    /**
     * Load more Pokemon for pagination.
     */
    fun loadMorePokemon() {
        if (isLoadingMore || !hasMoreData) return

        viewModelScope.launch {
            isLoadingMore = true
            _uiState.update { currentState ->
                if (currentState is HomeUiState.Success) currentState.copy(isLoadingMore = true)
                else currentState
            }

            getPokemonListUseCase(limit = pageSize, offset = currentOffset)
                .collectLatest { result: Result<List<PokemonListItem>> ->
                    handleResult(result, isInitialLoad = false)
                }
        }
    }

    /**
     * Handle API result and update UI state.
     */
    private fun handleResult(result: Result<List<PokemonListItem>>, isInitialLoad: Boolean) {
        when (result) {
            is Result.Success -> {
                val newData = result.data
                allPokemon = if (isInitialLoad) newData else allPokemon + newData
                hasMoreData = newData.size >= pageSize
                currentOffset += pageSize
                isLoadingMore = false

                _uiState.update {
                    HomeUiState.Success(
                        pokemonList = filterPokemon(allPokemon, _searchQuery.value),
                        hasMoreData = hasMoreData,
                        isLoadingMore = false
                    )
                }
            }

            is Result.Error -> {
                isLoadingMore = false
                val isNetwork = result.exception.isNetworkRelated()
                _uiState.update {
                    if (isInitialLoad) {
                        HomeUiState.Error(
                            message = result.exception.message ?: UNKNOWN_ERROR,
                            isNetworkError = isNetwork,
                            isRetryable = true
                        )
                    } else {
                        if (it is HomeUiState.Success) it.copy(isLoadingMore = false) else it
                    }
                }
            }

            is Result.Loading -> {
                if (isInitialLoad) _uiState.update { HomeUiState.Loading }
            }
        }
    }

    /**
     * Update search query (debounced).
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Observe debounced search queries.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                _uiState.update { currentState ->
                    if (currentState is HomeUiState.Success) {
                        currentState.copy(pokemonList = filterPokemon(allPokemon, query))
                    } else currentState
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Filter Pokemon by name or ID.
     */
    private fun filterPokemon(list: List<PokemonListItem>, query: String): List<PokemonListItem> {
        return if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.getId().contains(query, ignoreCase = true)
        }
    }

    /**
     * Refresh Pokemon list.
     */
    fun refresh() = loadPokemonList()

    /**
     * Check if more data can be loaded.
     */
    fun canLoadMore() = hasMoreData && !isLoadingMore
}

/**
 * Home screen UI state.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val pokemonList: List<PokemonListItem>,
        val hasMoreData: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : HomeUiState()
    data class Error(
        val message: String,
        val isNetworkError: Boolean = false,
        val isRetryable: Boolean = true
    ) : HomeUiState()
}

/**
 * Detect network-related errors.
 */
private fun Throwable.isNetworkRelated(): Boolean {
    val msg = message?.lowercase() ?: return false
    return listOf(KEYWORD_NO_INTERNET, KEYWORD_NETWORK, KEYWORD_TIMEOUT, KEYWORD_TIMEOUT).any { it in msg }

}
