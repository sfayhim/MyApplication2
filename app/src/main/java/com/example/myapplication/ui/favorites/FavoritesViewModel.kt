package com.example.myapplication.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.FavoriteRepository
import com.example.myapplication.data.room.AppDatabase
import com.example.myapplication.data.room.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoriteRepository(AppDatabase.getInstance(application))

    // StateFlow + collectAsStateWithLifecycle — notions du cours
    val favorites: StateFlow<List<FavoriteEntity>> = repository
        .getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isFavorite(id: String): kotlinx.coroutines.flow.Flow<Boolean> =
        repository.isFavorite(id)

    fun toggleFavorite(favorite: FavoriteEntity, currentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (currentlyFavorite) {
                repository.removeFavoriteById(favorite.id)
            } else {
                repository.addFavorite(favorite)
            }
        }
    }

    fun removeFavorite(id: String) {
        viewModelScope.launch {
            repository.removeFavoriteById(id)
        }
    }
}
