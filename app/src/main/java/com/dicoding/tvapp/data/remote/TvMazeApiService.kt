package com.dicoding.tvapp.data.remote

import com.dicoding.tvapp.data.model.TvShow
import com.dicoding.tvapp.data.model.TvShowDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApiService {

    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<TvShow>

    @GET("shows/{id}?embed[]=seasons&embed[]=episodes&embed[]=cast")
    suspend fun getShowDetail(@Path("id") id: Int): TvShowDetail
}
