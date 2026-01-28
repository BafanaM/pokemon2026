package com.example.landingmodule

import com.example.landingmodule.ui.HomeViewModel
import com.example.networkmodule.usecase.GetPokemonListUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.core.Result
import com.example.landingmodule.ui.HomeUiState
import com.example.networkmodule.model.PokemonListItem

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var mockGetPokemonListUseCase: GetPokemonListUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetPokemonListUseCase = mockk<GetPokemonListUseCase>()

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Success(emptyList()))

        viewModel = HomeViewModel(mockGetPokemonListUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPokemonList updates state to Success when use case succeeds`() = runTest {
        // Given
        val mockPokemonList = listOf(
            PokemonListItem("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
            PokemonListItem("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
        )

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Success(mockPokemonList))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val successState = viewModel.uiState.value as HomeUiState.Success
        assertEquals(2, successState.pokemonList.size)
        assertEquals("bulbasaur", successState.pokemonList[0].name)
        assertEquals("ivysaur", successState.pokemonList[1].name)
    }

    @Test
    fun `loadPokemonList detects network errors correctly`() = runTest {
        // Given
        val networkException = Exception("No internet connection")

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Error(networkException))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        val errorState = viewModel.uiState.value as HomeUiState.Error
        assertTrue(errorState.isNetworkError)
    }

    @Test
    fun `loadPokemonList detects generic network errors correctly`() = runTest {
        // Given
        val networkException = Exception("Network error occurred")

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Error(networkException))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        val errorState = viewModel.uiState.value as HomeUiState.Error
        assertTrue(errorState.isNetworkError)
    }

    @Test
    fun `updateSearchQuery updates searchQuery state`() = runTest {
        // Given
        val searchQuery = "pikachu"

        // When
        viewModel.updateSearchQuery(searchQuery)

        // Then
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `updateSearchQuery with empty string updates state correctly`() = runTest {
        // Given
        val emptySearchQuery = ""

        // When
        viewModel.updateSearchQuery(emptySearchQuery)

        // Then
        assertEquals(emptySearchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `updateSearchQuery with special characters updates state correctly`() = runTest {
        // Given
        val specialSearchQuery = "mr-mime"

        // When
        viewModel.updateSearchQuery(specialSearchQuery)

        // Then
        assertEquals(specialSearchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `updateSearchQuery with empty string shows all Pokemon`() = runTest {
        // Given
        val mockPokemonList = listOf(
            PokemonListItem("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
            PokemonListItem("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
        )

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Success(mockPokemonList))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("")

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val successState = viewModel.uiState.value as HomeUiState.Success
        assertEquals(2, successState.pokemonList.size)
    }

    @Test
    fun `loadPokemonList handles loading state correctly`() = runTest {
        // Given
        val mockPokemonList = listOf(
            PokemonListItem("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")
        )

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Success(mockPokemonList))

        // When
        viewModel.loadPokemonList()

        // Then
        // The loading state is set immediately when loadPokemonList is called
        // but then quickly transitions to success state
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
    }

    @Test
    fun `loadPokemonList handles error state correctly`() = runTest {
        // Given
        val exception = Exception("API Error")

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Error(exception))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        val errorState = viewModel.uiState.value as HomeUiState.Error
        assertEquals("API Error", errorState.message)
        assertTrue(errorState.isRetryable)
    }

    @Test
    fun `loadPokemonList handles timeout errors correctly`() = runTest {
        // Given
        val timeoutException = Exception("Request timeout")

        coEvery {
            mockGetPokemonListUseCase(any(), any())
        } returns flowOf(Result.Error(timeoutException))

        // When
        viewModel.loadPokemonList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        val errorState = viewModel.uiState.value as HomeUiState.Error
        assertTrue(errorState.isNetworkError)
    }
}