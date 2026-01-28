package com.example.networkmodule.usecase

import com.example.networkmodule.model.PokemonListItem
import com.example.networkmodule.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    /**
     * Execute the use case to fetch a paginated list of Pokemon.
     *
     * @param limit The maximum number of items to fetch (default 150)
     * @param offset The pagination offset (default 0)
     * @return Flow emitting Result with a list of Pokemon or error
     */
    operator fun invoke(
        limit: Int = DEFAULT_LIMIT,
        offset: Int = DEFAULT_OFFSET
    ): Flow<Result<List<PokemonListItem>>> = repository.getAll(limit, offset)

    private companion object {
        const val DEFAULT_LIMIT = 151
        const val DEFAULT_OFFSET = 0
    }
}
