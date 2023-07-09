package com.example.jetpackcomposepokemon.Util

private const val POKEMON_LIST_SCREEN ="pokemonListScreen"
private const val POKEMON_DETAILS_SCREEN ="pokemonDetailsScreen"

sealed class Screen(val route:String){
    object PokemonListScreen : Screen(POKEMON_LIST_SCREEN)
    object PokemonDetailsScreen : Screen(POKEMON_DETAILS_SCREEN)

    fun withArgs(vararg args : Any):String{
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/$arg")
            }
        }
    }
}
