package com.example.emtyapp.ui.screens.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.data.Repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var product = mutableStateOf<Product?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun loadProductById(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val products = repository.getProducts()
                product.value = products.find { it.id == id }
            } finally {
                isLoading.value = false
            }
        }
    }
}
