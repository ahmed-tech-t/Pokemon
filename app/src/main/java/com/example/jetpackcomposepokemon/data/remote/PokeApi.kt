package com.example.jetpackcomposepokemon.data.remote

import androidx.compose.ui.geometry.Offset
import com.example.jetpackcomposepokemon.data.remote.response.Pokemon
import com.example.jetpackcomposepokemon.data.remote.response.PokemonList
import retrofit2.http.GET
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(
        @Path("name") name: String
    ): Pokemon
}