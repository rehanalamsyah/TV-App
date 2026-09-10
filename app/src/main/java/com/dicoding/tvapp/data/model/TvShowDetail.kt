package com.dicoding.tvapp.data.model

import com.google.gson.annotations.SerializedName

data class TvShowDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("summary") val summary: String?,
    @SerializedName("premiered") val premiered: String?,
    @SerializedName("image") val image: Image?,
    @SerializedName("rating") val rating: Rating?,
    @SerializedName("url") val url: String?
)
