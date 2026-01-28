package com.example.core

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {
    suspend fun getAll(): Flow<Result<List<T>>>
}