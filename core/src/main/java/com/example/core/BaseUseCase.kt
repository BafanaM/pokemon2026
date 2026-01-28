package com.example.core

import kotlinx.coroutines.flow.Flow

interface BaseUseCase<P, R> {

    /**
     * Execute the use case with the given parameters.
     * @param params The parameters required for the use case
     * @return Flow of Result containing the result or error
     */
    suspend fun execute(params: P): Flow<Result<R>>
}

/**
 * Base interface for use cases that don't require parameters.
 *
 * @param R The type of result the use case returns
 */
interface BaseUseCaseNoParams<R> {

    /**
     * Execute the use case without parameters.
     * @return Flow of Result containing the result or error
     */
    suspend fun execute(): Flow<Result<R>>
}