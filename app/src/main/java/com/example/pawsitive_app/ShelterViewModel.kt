package com.example.pawsitive_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShelterViewModel : ViewModel() {
    private val repository = ShelterRepository()

    private val _newsState = MutableStateFlow<List<NewsPost>>(emptyList())
    val newsState: StateFlow<List<NewsPost>> = _newsState.asStateFlow()

    private val _dogsState = MutableStateFlow<List<Dog>>(emptyList())
    val dogsState: StateFlow<List<Dog>> = _dogsState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val news = repository.getNewsPosts()
            val dogs = repository.getDogs()
            
            _newsState.value = news
            _dogsState.value = dogs
            
            _isLoading.value = false
        }
    }

    fun toggleFavorite(dogId: String) {
        val currentList = _dogsState.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == dogId }
        if (index != -1) {
            val dog = currentList[index]
            currentList[index] = dog.copy(isFavorite = !dog.isFavorite)
            _dogsState.value = currentList
        }
    }
}
