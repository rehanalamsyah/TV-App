package com.dicoding.tvapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tvapp.data.model.TvShowDetail
import com.dicoding.tvapp.data.remote.RetrofitInstance
import com.dicoding.tvapp.data.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ShowDetailUiState {
    object Loading : ShowDetailUiState()
    data class Success(val show: TvShowDetail) : ShowDetailUiState()
    data class Error(val message: String) : ShowDetailUiState()
}

class ShowDetailViewModel(
    private val repository: TvShowRepository = TvShowRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowDetailUiState>(ShowDetailUiState.Loading)
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    fun loadShowDetail(id: Int) {
        _uiState.value = ShowDetailUiState.Loading
        viewModelScope.launch {
            repository.getShowDetail(id)
                .onSuccess { show ->
                    _uiState.value = ShowDetailUiState.Success(show)
                }
                .onFailure { error ->
                    _uiState.value = ShowDetailUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }
}
