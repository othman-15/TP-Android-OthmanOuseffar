package com.example.emtyapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Repository.ProductRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch



@HiltViewModel
class HomeViewModel @Inject constructor( private val repository : ProductRepository) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadProducts -> loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // simulate loading
                val products = repository.getProducts()
                _state.value = HomeState(products = products)
            } catch (e: Exception) {
                _state.value = HomeState(error = e.message)
            }
        }
    }
}
