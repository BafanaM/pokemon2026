package com.example.core

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {
    fun getAll(): Flow<Result<List<T>>>
}