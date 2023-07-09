package com.example.jetpackcomposepokemon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcomposepokemon.Util.Screen
import com.example.jetpackcomposepokemon.pokemomDetails.PokemonDetailsScreen
import com.example.jetpackcomposepokemon.ui.pokemonList.PokemonListScreen
import java.util.Locale

private const val NAME = "name"
private const val COLOR = "color"

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PokemonListScreen.route) {
        composable(route = Screen.PokemonListScreen.route) {
            PokemonListScreen(navController)
        }
        composable(route = Screen.PokemonDetailsScreen.route + "/{$NAME}/{$COLOR}",
            arguments = listOf(
                navArgument(NAME) {
                    type = NavType.StringType
                },
                navArgument(COLOR) {
                    type = NavType.IntType
                }
            )) {
            val color = remember {
                val c = it.arguments?.getInt(COLOR)
                c?.let { Color(it) } ?: Color.White
            }
            val name = remember {
                it.arguments?.getString(NAME) ?: ""
            }
            PokemonDetailsScreen(
                pokemonName = name.toLowerCase(Locale.ROOT),
                dominantColor = color,
                navController = navController
            )
        }
    }
}