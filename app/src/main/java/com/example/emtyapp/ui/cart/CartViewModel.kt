package com.example.emtyapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CartViewState>(CartViewState.Loading)
    val state: StateFlow<CartViewState> = _state.asStateFlow()

    init {
        loadCart()
    }

    fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> loadCart()
            is CartIntent.AddToCart -> addToCart(intent.product)
            is CartIntent.UpdateQuantity -> updateQuantity(intent.cartItemId, intent.quantity)
            is CartIntent.RemoveItem -> removeItem(intent.cartItemId)
            is CartIntent.ClearCart -> clearCart()
            is CartIntent.Checkout -> checkout()
        }
    }

    private fun addToCart(product: com.example.emtyapp.data.Entities.Product) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(product)

            } catch (e: Exception) {
                _state.value = CartViewState.Error("Erreur lors de l'ajout au panier: ${e.message}")
            }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCartItems()
                .catch { e ->
                    _state.value = CartViewState.Error("Erreur lors du chargement du panier: ${e.message}")
                }
                .collect { items ->
                    if (items.isEmpty()) {
                        _state.value = CartViewState.EmptyCart
                    } else {
                        val total = cartRepository.getCartTotal()
                        val count = cartRepository.getCartItemsCount()
                        _state.value = CartViewState.CartLoaded(items, total, count)
                    }
                }
        }
    }

    private fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.updateQuantity(cartItemId, quantity)

            } catch (e: Exception) {
                _state.value = CartViewState.Error("Erreur lors de la mise à jour: ${e.message}")
            }
        }
    }

    private fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(cartItemId)

            } catch (e: Exception) {
                _state.value = CartViewState.Error("Erreur lors de la suppression: ${e.message}")
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            try {
                cartRepository.clearCart()

            } catch (e: Exception) {
                _state.value = CartViewState.Error("Erreur lors de la suppression du panier: ${e.message}")
            }
        }
    }

    private fun checkout() {
        viewModelScope.launch {
            try {

                kotlinx.coroutines.delay(2000)
                cartRepository.clearCart()
                _state.value = CartViewState.CheckoutSuccess
            } catch (e: Exception) {
                _state.value = CartViewState.Error("Erreur lors de la commande: ${e.message}")
            }
        }
    }
}