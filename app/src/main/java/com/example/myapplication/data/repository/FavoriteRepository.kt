package com.example.myapplication.data.repository

import com.example.myapplication.data.room.AppDatabase
import com.example.myapplication.data.room.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val db: AppDatabase) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> =
        db.favoriteDao().getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> =
        db.favoriteDao().isFavorite(id)

    suspend fun addFavorite(favorite: FavoriteEntity) =
        db.favoriteDao().addFavorite(favorite)

    suspend fun removeFavoriteById(id: String) =
        db.favoriteDao().removeFavoriteById(id)
}
