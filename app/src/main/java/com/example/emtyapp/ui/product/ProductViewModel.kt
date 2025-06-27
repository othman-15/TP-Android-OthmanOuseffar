package com.example.emtyapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.data.Repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProductViewState>(ProductViewState.Loading)
    val state: StateFlow<ProductViewState> = _state

    suspend fun getProductById(id: String): Product? {
        return repository.getProductById(id).getOrNull()
    }

    fun handleIntent(intent: ProductIntent) {
        when (intent) {
            is ProductIntent.LoadProducts -> loadProducts()
            is ProductIntent.RefreshProducts -> refreshProducts()
            is ProductIntent.SearchProducts -> searchProducts(intent.query)
        }
    }

    private fun loadProducts() {
        if (_state.value is ProductViewState.ProductsLoaded) {
            return
        }

        _state.update { ProductViewState.Loading }

        viewModelScope.launch {
            repository.getProducts()
                .onSuccess { products ->
                    _state.update {
                        ProductViewState.ProductsLoaded(products)
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        ProductViewState.Error(
                            exception.message ?: "Erreur de chargement des produits"
                        )
                    }
                }
        }
    }

    private fun refreshProducts() {
        _state.update { ProductViewState.Loading }
        loadProducts()
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            repository.getProducts()
                .onSuccess { products ->
                    val filteredProducts = if (query.isBlank()) {
                        products
                    } else {
                        products.filter { product ->
                            product.name.contains(query, ignoreCase = true) ||
                                    product.categorie?.contains(query, ignoreCase = true) == true
                        }
                    }
                    _state.update {
                        ProductViewState.ProductsLoaded(filteredProducts)
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        ProductViewState.Error(
                            exception.message ?: "Erreur de recherche"
                        )
                    }
                }
        }
    }
}
