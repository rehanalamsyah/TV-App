package com.dicoding.tvapp.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.tvapp.data.model.Image
import com.dicoding.tvapp.data.model.Rating
import com.dicoding.tvapp.data.model.TvShow
import com.dicoding.tvapp.data.repository.TvShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ShowListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TvShowRepository
    private lateinit var viewModel: ShowListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadShows success - uiState becomes Success with shows list`() = runTest {
        // Given
        val fakeShows = listOf(
            TvShow(
                id = 1,
                name = "Breaking Bad",
                image = Image(medium = "https://example.com/medium.jpg", original = null),
                rating = Rating(average = 9.5),
                genres = listOf("Crime", "Drama"),
                premiered = "2008-01-20"
            ),
            TvShow(
                id = 2,
                name = "Game of Thrones",
                image = null,
                rating = Rating(average = null),
                genres = listOf("Action", "Adventure"),
                premiered = "2011-04-17"
            )
        )
        whenever(repository.getShows(0)).thenReturn(Result.success(fakeShows))

        // When
        viewModel = ShowListViewModel(repository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("Expected Success state", state is ShowListUiState.Success)
        val successState = state as ShowListUiState.Success
        assertEquals(2, successState.shows.size)
        assertEquals("Breaking Bad", successState.shows[0].name)
        assertEquals(9.5, successState.shows[0].rating?.average)
    }

    @Test
    fun `loadShows failure - uiState becomes Error with message`() = runTest {
        // Given
        val errorMessage = "Network error: Unable to connect"
        whenever(repository.getShows(0)).thenReturn(
            Result.failure(Exception(errorMessage))
        )

        // When
        viewModel = ShowListViewModel(repository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("Expected Error state", state is ShowListUiState.Error)
        val errorState = state as ShowListUiState.Error
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `search filter - filters shows by title or genre correctly`() = runTest {
        // Given
        val fakeShows = listOf(
            TvShow(
                id = 1,
                name = "Under the Dome",
                image = null,
                rating = Rating(average = 6.6),
                genres = listOf("Drama", "Science-Fiction"),
                premiered = "2013-06-24"
            ),
            TvShow(
                id = 2,
                name = "Person of Interest",
                image = null,
                rating = Rating(average = 8.8),
                genres = listOf("Action", "Crime"),
                premiered = "2011-09-22"
            )
        )
        whenever(repository.getShows(0)).thenReturn(Result.success(fakeShows))

        viewModel = ShowListViewModel(repository)
        advanceUntilIdle()

        // When searching by title
        viewModel.onSearchQueryChange("Dome")
        advanceUntilIdle()

        val filteredByTitle = viewModel.uiState.value as ShowListUiState.Success
        assertEquals(1, filteredByTitle.shows.size)
        assertEquals("Under the Dome", filteredByTitle.shows[0].name)

        // When searching by genre
        viewModel.onSearchQueryChange("Crime")
        advanceUntilIdle()

        val filteredByGenre = viewModel.uiState.value as ShowListUiState.Success
        assertEquals(1, filteredByGenre.shows.size)
        assertEquals("Person of Interest", filteredByGenre.shows[0].name)
    }

    @Test
    fun `loadShows with null rating - handled without crash`() = runTest {
        val showsWithNullRating = listOf(
            TvShow(id = 42, name = "Some Show", image = null, rating = null),
            TvShow(id = 43, name = "Another Show", image = null, rating = Rating(average = null))
        )
        whenever(repository.getShows(0)).thenReturn(Result.success(showsWithNullRating))

        viewModel = ShowListViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ShowListUiState.Success)
        val shows = (state as ShowListUiState.Success).shows
        assertEquals(null, shows[0].rating)
        assertEquals(null, shows[1].rating?.average)
    }
}
