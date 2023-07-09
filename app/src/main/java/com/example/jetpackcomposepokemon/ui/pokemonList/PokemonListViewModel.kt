package com.example.jetpackcomposepokemon.ui.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.jetpackcomposepokemon.Util.Constants.PAGE_SIZE
import com.example.jetpackcomposepokemon.Util.Resource
import com.example.jetpackcomposepokemon.data.models.PokemonEntryList
import com.example.jetpackcomposepokemon.repo.PokemonRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repo: PokemonRepo
) : ViewModel() {

    var curPage = 0;
    var pokemonList = mutableStateOf<List<PokemonEntryList>>(listOf())
    var loadingError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    var cashingList = listOf<PokemonEntryList>()
    var isSearchString = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun search(query: String) {
        val listToSearch = if (isSearchString) {
            pokemonList.value
        } else cashingList

        if (isSearchString) {
            cashingList = pokemonList.value
            isSearchString = false
        }

        viewModelScope.launch(Dispatchers.Default) {
            if (listToSearch.isEmpty()) {
                isSearching.value = false
                isSearchString = true
                pokemonList.value = cashingList
                return@launch
            }
            val result = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }

            pokemonList.value = result
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repo.getPokemonList(curPage, curPage * PAGE_SIZE)
            when (result) {
                is Resource.Error -> {
                    loadingError.value = result.message!!
                    isLoading.value = false
                }

                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, result ->
                        val number = if (result.url.endsWith("/")) {
                            result.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            result.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonEntryList(result.name, url, number.toInt())
                    }
                    curPage++
                    pokemonList.value += pokedexEntries
                    isLoading.value = false
                    loadingError.value = ""
                }

                is Resource.Loading -> TODO()
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}