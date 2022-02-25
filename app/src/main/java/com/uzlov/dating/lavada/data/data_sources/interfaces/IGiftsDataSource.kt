package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.CategoryGifts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface IGiftsDataSource {

    @ExperimentalCoroutinesApi
    suspend fun getCategoryGifts(): Flow<List<CategoryGifts>>

    @ExperimentalCoroutinesApi
    suspend fun getCategoryByID(id: String): Flow<CategoryGifts?>
}