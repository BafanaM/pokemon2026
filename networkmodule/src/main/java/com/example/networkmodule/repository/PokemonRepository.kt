package com.example.networkmodule.repository

import com.example.core.BaseRepository
import com.example.core.NetworkUtils
import com.example.networkmodule.NetworkErrorHandler
import com.example.networkmodule.api.PokeApiService
import com.example.networkmodule.constants.NetworkConstants
import com.example.networkmodule.model.PokemonDetailResponse
import com.example.networkmodule.model.PokemonListItem
import com.example.core.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val pokeApiService: PokeApiService,
    private val networkUtils: NetworkUtils
) : BaseRepository<PokemonListItem> {
    
    /**
     * Get all Pokemon from the API.
     * @return Flow of Result containing list of Pokemon or error
     */
    override suspend fun getAll(): Flow<Result<List<PokemonListItem>>> = flow {
        emit(Result.Loading)
        
        // Check network connectivity first
        if (!networkUtils.isNetworkAvailable()) {
            emit(Result.Error(Exception(NetworkConstants.NO_INTERNET_AVAILABLE)))
            return@flow
        }
        
        val response = pokeApiService.getPokemonList(limit = NetworkConstants.DEFAULT_LIMIT, offset = NetworkConstants.DEFAULT_OFFSET)
        emit(Result.Success(response.results))
    }.catch { e ->
        val errorMessage = NetworkErrorHandler.handleException(e)
        emit(Result.Error(Exception(errorMessage)))
    }

    /**
     * Get detailed Pokemon information by ID or name.
     * @param idOrName The Pokemon ID or name
     * @return Flow of Result containing detailed Pokemon information or error
     */
    fun getPokemonDetail(idOrName: String): Flow<Result<PokemonDetailResponse>> = flow {
        emit(Result.Loading)

        if (!networkUtils.isNetworkAvailable()) {
            emit(Result.Error(Exception(NetworkConstants.NO_INTERNET_AVAILABLE)))
            return@flow
        }
        
        val response = pokeApiService.getPokemonDetail(idOrName)
        emit(Result.Success(response))
    }.catch { e ->
        val errorMessage = NetworkErrorHandler.handleException(e)
        emit(Result.Error(Exception(errorMessage)))
    }
}
