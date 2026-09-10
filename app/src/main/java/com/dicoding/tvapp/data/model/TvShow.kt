package com.dicoding.tvapp.data.model

import com.google.gson.annotations.SerializedName

data class TvShow(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: Image?,
    @SerializedName("rating") val rating: Rating?
)

data class Image(
    @SerializedName("medium") val medium: String?,
    @SerializedName("original") val original: String?
)

data class Rating(
    @SerializedName("average") val average: Double?
)
