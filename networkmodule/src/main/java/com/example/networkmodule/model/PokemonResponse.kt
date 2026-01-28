package com.example.networkmodule.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonListResponse(
    @Json(name = "count") val count: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "previous") val previous: String?,
    @Json(name = "results") val results: List<PokemonListItem>
)

@JsonClass(generateAdapter = true)
data class PokemonListItem(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
) {
    fun getId(): String {
        return url.split("/").dropLast(1).last()
    }
}

@JsonClass(generateAdapter = true)
data class PokemonDetailResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "base_experience") val baseExperience: Int?,
    @Json(name = "height") val height: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "sprites") val sprites: Sprites,
    @Json(name = "stats") val stats: List<Stat>,
    @Json(name = "types") val types: List<Type>,
    @Json(name = "abilities") val abilities: List<Ability>,
    @Json(name = "forms") val forms: List<Form>
)

@JsonClass(generateAdapter = true)
data class Sprites(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?,
    @Json(name = "back_default") val backDefault: String?,
    @Json(name = "back_shiny") val backShiny: String?,
    @Json(name = "other") val other: OtherSprites?
)

@JsonClass(generateAdapter = true)
data class OtherSprites(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtwork?
)

@JsonClass(generateAdapter = true)
data class OfficialArtwork(
    @Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class Stat(
    @Json(name = "base_stat") val baseStat: Int,
    @Json(name = "effort") val effort: Int,
    @Json(name = "stat") val stat: StatInfo
)

@JsonClass(generateAdapter = true)
data class StatInfo(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class Type(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: TypeInfo
)

@JsonClass(generateAdapter = true)
data class TypeInfo(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class Ability(
    @Json(name = "ability") val ability: AbilityInfo,
    @Json(name = "is_hidden") val isHidden: Boolean,
    @Json(name = "slot") val slot: Int
)

@JsonClass(generateAdapter = true)
data class AbilityInfo(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class Form(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

