package com.example.jetpackcomposepokemon.repo

import com.example.jetpackcomposepokemon.Util.Resource
import com.example.jetpackcomposepokemon.data.remote.PokeApi
import com.example.jetpackcomposepokemon.data.remote.response.Pokemon
import com.example.jetpackcomposepokemon.data.remote.response.PokemonList
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class PokemonRepo @Inject constructor(
    private val api: PokeApi
) {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (ex: Exception) {
            return Resource.Error(message = "Error ${ex.message} ")
        }

        return Resource.Success(response)
    }

    suspend fun getPokemon(name: String): Resource<Pokemon> {
        val response = try {
            api.getPokemon(name)
        } catch (ex: Exception) {
            return Resource.Error(message = "Error ${ex.message} ")
        }
        return Resource.Success(response)
    }
}