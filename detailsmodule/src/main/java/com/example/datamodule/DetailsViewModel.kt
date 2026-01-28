package com.example.datamodule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamodule.constants.InfoConstants.KEY_POKEMON_ID
import com.example.datamodule.constants.InfoConstants.KEY_POKEMON_ID_ERROR
import com.example.networkmodule.usecase.GetPokemonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.core.Result
import com.example.datamodule.constants.InfoConstants.ERROR_NETWORK
import com.example.datamodule.constants.InfoConstants.ERROR_NO_INTERNET
import com.example.datamodule.constants.InfoConstants.ERROR_TIMEOUT
import com.example.datamodule.constants.InfoConstants.ERROR_UNKNOWN
import com.example.networkmodule.model.PokemonDetailResponse

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<InfoUiState>(InfoUiState.Loading)
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    private val pokemonId: String =
        savedStateHandle.get<String>(KEY_POKEMON_ID)
            ?: error(KEY_POKEMON_ID_ERROR)

    init {
        loadPokemonDetail()
    }

    fun loadPokemonDetail() {
        viewModelScope.launch {
            getPokemonDetailUseCase.execute(pokemonId)
                .onStart { _uiState.value = InfoUiState.Loading }
                .catch { e -> _uiState.value = InfoUiState.Error(e.message, e.isNetworkError()) }
                .collectLatest { result ->
                    _uiState.value = when (result) {
                        is Result.Success -> InfoUiState.Success(result.data)
                        is Result.Error -> InfoUiState.Error(
                            message = result.exception.message,
                            isNetworkError = result.exception.isNetworkError()
                        )

                        is Result.Loading -> InfoUiState.Loading
                    }
                }
        }
    }

    fun refresh() = loadPokemonDetail()
}

private fun Throwable.isNetworkError(): Boolean =
    message?.contains(ERROR_NO_INTERNET, ignoreCase = true) == true ||
            message?.contains(ERROR_NETWORK, ignoreCase = true) == true ||
            message?.contains(ERROR_TIMEOUT, ignoreCase = true) == true

sealed interface InfoUiState {
    object Loading : InfoUiState
    data class Success(val pokemon: PokemonDetailResponse) : InfoUiState
    data class Error(
        val message: String? = ERROR_UNKNOWN,
        val isNetworkError: Boolean = false,
        val isRetryable: Boolean = true
    ) : InfoUiState
}
