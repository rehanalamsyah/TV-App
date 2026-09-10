package com.dicoding.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tvapp.data.model.TvShow
import com.dicoding.tvapp.data.remote.RetrofitInstance
import com.dicoding.tvapp.data.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ShowListUiState {
    object Loading : ShowListUiState()
    data class Success(val shows: List<TvShow>) : ShowListUiState()
    data class Error(val message: String) : ShowListUiState()
}

class ShowListViewModel(
    private val repository: TvShowRepository = TvShowRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowListUiState>(ShowListUiState.Loading)
    val uiState: StateFlow<ShowListUiState> = _uiState.asStateFlow()

    init {
        loadShows()
    }

    fun loadShows(page: Int = 0) {
        _uiState.value = ShowListUiState.Loading
        viewModelScope.launch {
            repository.getShows(page)
                .onSuccess { shows ->
                    _uiState.value = ShowListUiState.Success(shows)
                }
                .onFailure { error ->
                    _uiState.value = ShowListUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }
}
