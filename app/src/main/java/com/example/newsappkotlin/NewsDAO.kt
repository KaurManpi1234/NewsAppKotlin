package com.example.newsappkotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticle(articles: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticles(articles: List<Article>)

    @Query("DELETE FROM article_table")
    suspend fun deleteAllArticles()

    @Query("DELETE FROM article_table WHERE category = :category")
    fun deleteArticlesFor(category: String)

    @Query("SELECT * FROM article_table WHERE category = :category")
    fun getAllArticlesUsingCategory(category: String): Flow<List<Article>>

    @Query("SELECT * FROM article_table")
    fun getAllArticles(): Flow<List<Article>>
}