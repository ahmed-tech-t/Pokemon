package com.example.jetpackcomposepokemon.ui.pokemonList

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.jetpackcomposepokemon.R
import com.example.jetpackcomposepokemon.Util.Screen
import com.example.jetpackcomposepokemon.data.models.PokemonEntryList
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlin.concurrent.timer

private const val TAG = "PokemonListScreen"
@Composable
fun PokemonListScreen(
    navController: NavController ,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokemon_logo),
                contentDescription = "pokemon logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(100.dp)
                    .padding(5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar(hint = "Search") {query ->
               viewModel.search(query)
            }
            Spacer(modifier = Modifier.height(5.dp))
            EntryList(navController = navController)
        }
    }
}

@Composable
fun ProgressBar() {
    Box(contentAlignment = Center, modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.scale(1f)
        )
    }
}

@Composable
fun EntryList(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadingError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    EntryGridList(
        list = pokemonList,
        endReached = endReached,
        navController = navController,
        modifier = modifier ,
        loading = isLoading ,
        isSearching = isSearching
    )
  Box(modifier = Modifier.fillMaxSize() , contentAlignment = Center){
      if (isLoading) {
          ProgressBar()
      }
      if (loadError.isNotEmpty()) {
          RetrySection(error = loadError) {
              viewModel.loadPokemonPaginated()
          }
      }
  }
}

@Composable
fun EntryGridList(
    list: List<PokemonEntryList>,
    endReached: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier,
    loading:Boolean,
    isSearching:Boolean,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = modifier) {
        items(list.size) { index ->
            val pokemon = list[index]
            if (index >= list.size - 1 && !endReached && !loading && !isSearching ) viewModel.loadPokemonPaginated()
            Item(pokemon = pokemon, navController = navController)
        }
    }
}

@Composable
fun Item(
    pokemon: PokemonEntryList,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominationColor = MaterialTheme.colorScheme.surface
    var dominationColor by remember {
        mutableStateOf(defaultDominationColor)
    }
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        dominationColor, defaultDominationColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    Screen.PokemonDetailsScreen.withArgs(
                        pokemon.pokemonName, dominationColor.toArgb()
                    )
                )
            }) {
        Column {
            val painter = rememberAsyncImagePainter(model = pokemon.imageUrl,
                imageLoader = ImageLoader.Builder(LocalContext.current).crossfade(true).build(),
                onSuccess = { result ->
                    viewModel.calcDominantColor(result.result.drawable) { color ->
                        dominationColor = color
                    }
                })
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center){
              Image(
                  painter = painter,
                  contentDescription = "images",
                  modifier = Modifier
                      .size(120.dp)
              )
              if (painter.state is AsyncImagePainter.State.Loading) {
                  Box(contentAlignment = Center, modifier = Modifier.fillMaxSize()) {
                      CircularProgressIndicator(
                          color = MaterialTheme.colorScheme.primary
                      )
                  }
              }
          }
            Text(
                text = pokemon.pokemonName,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier, hint: String = "", onSearch: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            textStyle = TextStyle(
                color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Medium
            ),
            singleLine = true,
            maxLines = 1,
            modifier = modifier
                .shadow(5.dp, CircleShape)
                .fillMaxWidth()
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                })
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onRetry() }) {
            Text(text = "Retry", fontSize = 18.sp)
        }
    }
}