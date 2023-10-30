package com.example.newsappkotlin

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Article::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class NewsDatabase:RoomDatabase() {

    abstract fun getNewsDAO(): NewsDAO
}