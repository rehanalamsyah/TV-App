package com.dicoding.tvapp.data.model

import com.google.gson.annotations.SerializedName

data class TvShowDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("summary") val summary: String?,
    @SerializedName("premiered") val premiered: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("image") val image: Image?,
    @SerializedName("rating") val rating: Rating?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("status") val status: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("network") val network: NetworkInfo?,
    @SerializedName("url") val url: String?,
    @SerializedName("_embedded") val embedded: EmbeddedData?
)

data class NetworkInfo(
    @SerializedName("name") val name: String?
)

data class EmbeddedData(
    @SerializedName("seasons") val seasons: List<Season>?,
    @SerializedName("episodes") val episodes: List<Episode>?,
    @SerializedName("cast") val cast: List<CastMember>?
)

data class Season(
    @SerializedName("id") val id: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("episodeOrder") val episodeOrder: Int?,
    @SerializedName("premiereDate") val premiereDate: String?
)

data class Episode(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("season") val season: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("rating") val rating: Rating?,
    @SerializedName("image") val image: Image?,
    @SerializedName("summary") val summary: String?
)

data class CastMember(
    @SerializedName("person") val person: Person,
    @SerializedName("character") val character: Character
)

data class Person(
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: Image?
)

data class Character(
    @SerializedName("name") val name: String
)
