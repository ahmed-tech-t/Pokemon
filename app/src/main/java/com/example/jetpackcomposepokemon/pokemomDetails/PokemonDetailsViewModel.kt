package com.example.jetpackcomposepokemon.pokemomDetails

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposepokemon.repo.PokemonRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val repo: PokemonRepo
) : ViewModel() {

    suspend fun getPokemonInfo(name:String) = repo.getPokemon(name)

}