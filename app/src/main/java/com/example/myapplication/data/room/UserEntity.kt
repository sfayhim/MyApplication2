package com.example.myapplication.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,          // id ou nctId de l'élément
    val type: String,                     // "research", "clinicalTrials", "fdaApprovals", "news", "blog"
    val title: String,
    val subtitle: String = "",
    val savedAt: Long = System.currentTimeMillis()
)
