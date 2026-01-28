package com.example.pokemonchallenge.ui

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Pokemon application.
 * This class is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class PokemonApplication : Application()

