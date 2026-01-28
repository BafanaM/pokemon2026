package com.example.networkmodule.api

import com.example.networkmodule.constants.NetworkConstants
import com.example.networkmodule.model.PokemonDetailResponse
import com.example.networkmodule.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for the PokeAPI.
 * This service provides methods to interact with the Pokemon API endpoints.
 */
interface PokeApiService {
    
    /**
     * Get a list of Pokemon with pagination support.
     * @param limit The number of Pokemon to return (max 100)
     * @param offset The offset for pagination
     * @return PokemonListResponse containing the list of Pokemon
     */
    @GET(NetworkConstants.POKEMON_ENDPOINT)
    suspend fun getPokemonList(
        @Query(NetworkConstants.LIMIT_PARAM) limit: Int = NetworkConstants.DEFAULT_LIMIT,
        @Query(NetworkConstants.OFFSET_PARAM) offset: Int = NetworkConstants.DEFAULT_OFFSET
    ): PokemonListResponse
    
    /**
     * Get detailed information about a specific Pokemon by its ID or name.
     * @param idOrName The Pokemon ID or name
     * @return PokemonDetailResponse containing detailed Pokemon information
     */
    @GET("${NetworkConstants.POKEMON_ENDPOINT}/{${NetworkConstants.ID_OR_NAME_PATH}}")
    suspend fun getPokemonDetail(
        @Path(NetworkConstants.ID_OR_NAME_PATH) idOrName: String
    ): PokemonDetailResponse
}

