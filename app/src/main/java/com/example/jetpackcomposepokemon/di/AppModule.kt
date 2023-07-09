package com.example.jetpackcomposepokemon.di

import com.example.jetpackcomposepokemon.Util.Constants.BASE_URL
import com.example.jetpackcomposepokemon.data.remote.PokeApi
import com.example.jetpackcomposepokemon.repo.PokemonRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
//    @Singleton
//    @Provides
//    fun providePokemonRepository(
//        api: PokeApi
//    ) = PokemonRepo(api)

    @Provides
    @Singleton
    fun providePokemonApi(): PokeApi = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(PokeApi::class.java)

}