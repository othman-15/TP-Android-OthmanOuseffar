package com.example.emtyapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Entities.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.emtyapp.data.Repository.ProductRepository  // ✅ Correct
import kotlinx.coroutines.flow.update

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<ProductViewState>(ProductViewState.Loading)
    val state: StateFlow<ProductViewState> = _state

    fun handleIntent(intent: ProductIntent) {
        when (intent) {
            is ProductIntent.LoadProducts -> loadProducts()
            else -> {}
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = repository.getProducts()
                _state.value = ProductViewState.ProductsLoaded(products)
            } catch (e: Exception) {
                _state.value = ProductViewState.Error("Erreur de chargement")
            }
        }
    }
}