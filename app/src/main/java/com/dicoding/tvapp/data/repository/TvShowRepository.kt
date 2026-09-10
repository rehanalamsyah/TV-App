package com.dicoding.tvapp.data.repository

import com.dicoding.tvapp.data.model.TvShow
import com.dicoding.tvapp.data.model.TvShowDetail
import com.dicoding.tvapp.data.remote.TvMazeApiService

class TvShowRepository(
    private val apiService: TvMazeApiService
) {
    suspend fun getShows(page: Int = 0): Result<List<TvShow>> {
        return runCatching {
            apiService.getShows(page)
        }
    }

    suspend fun getShowDetail(id: Int): Result<TvShowDetail> {
        return runCatching {
            apiService.getShowDetail(id)
        }
    }
}
