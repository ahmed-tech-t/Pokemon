package com.example.jetpackcomposepokemon.data.remote.response

import com.google.gson.annotations.SerializedName

class IconsX(
    @SerializedName("front_default")
    val frontDefault: String,
    @SerializedName("front_female")
    val frontFemale: Any
)
