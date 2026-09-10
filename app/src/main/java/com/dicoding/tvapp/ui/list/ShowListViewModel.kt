package com.dicoding.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tvapp.data.model.TvShow
import com.dicoding.tvapp.data.remote.RetrofitInstance
import com.dicoding.tvapp.data.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ShowListUiState {
    object Loading : ShowListUiState()
    data class Success(
        val shows: List<TvShow>,
        val totalCount: Int
    ) : ShowListUiState()
    data class Error(val message: String) : ShowListUiState()
}

class ShowListViewModel(
    private val repository: TvShowRepository = TvShowRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _rawState = MutableStateFlow<ShowListUiState>(ShowListUiState.Loading)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<ShowListUiState> = combine(_rawState, _searchQuery) { rawState, query ->
        if (rawState is ShowListUiState.Success) {
            val trimmed = query.trim()
            val filtered = if (trimmed.isEmpty()) {
                rawState.shows
            } else {
                rawState.shows.filter { show ->
                    show.name.contains(trimmed, ignoreCase = true) ||
                            (show.genres?.any { it.contains(trimmed, ignoreCase = true) } == true)
                }
            }
            ShowListUiState.Success(
                shows = filtered,
                totalCount = rawState.totalCount
            )
        } else {
            rawState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ShowListUiState.Loading
    )

    init {
        loadShows()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadShows(page: Int = 0) {
        _rawState.value = ShowListUiState.Loading
        viewModelScope.launch {
            repository.getShows(page)
                .onSuccess { shows ->
                    _rawState.value = ShowListUiState.Success(
                        shows = shows,
                        totalCount = shows.size
                    )
                }
                .onFailure { error ->
                    _rawState.value = ShowListUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }
}
