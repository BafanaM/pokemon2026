package com.example.networkmodule.usecase

import com.example.core.BaseUseCase
import com.example.networkmodule.model.PokemonDetailResponse
import com.example.networkmodule.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPokemonDetailUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : BaseUseCase<String, PokemonDetailResponse> {
    
    /**
     * Execute the use case to get detailed Pokemon information.
     * @param params The Pokemon ID or name
     * @return Flow of Result containing detailed Pokemon information or error
     */
    override suspend fun execute(params: String): Flow<Result<PokemonDetailResponse>> {
        return pokemonRepository.getPokemonDetail(params)
    }
}

