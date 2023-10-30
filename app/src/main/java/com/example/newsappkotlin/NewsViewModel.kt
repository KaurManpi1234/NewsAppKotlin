package com.example.newsappkotlin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    private val _news: MutableLiveData<Resource<List<Article>>> = MutableLiveData()
    val news: LiveData<Resource<List<Article>>> = _news
    private var allNewsDisplayed = mutableListOf<Article>()

    fun getNews(context: Context, category: String, pageNo: Int, isNew: Boolean) = viewModelScope.launch {
        _news.postValue(Resource.Loading())

        if (isNew) {
            allNewsDisplayed.clear()
        }
        repository.getNews(context, category, pageNo)
            .onStart {
                Resource.Loading<NewsResponse>()
            }
            .flowOn(Dispatchers.IO)
            .catch {
                _news.postValue(Resource.Error("No internet connection"))
            }
            .collect {

                if (it.data?.articles != null) {

                    allNewsDisplayed.addAll(it.data.articles)
                    _news.postValue(Resource.Success(allNewsDisplayed.toList()))
                } else {
                    _news.postValue(Resource.Error("No internet connection"))
                }
            }
    }
}