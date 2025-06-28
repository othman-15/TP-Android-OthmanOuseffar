package com.example.emtyapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Repository.CartRepository
import com.example.emtyapp.data.Repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CartViewState>(CartViewState.Loading)
    val state: StateFlow<CartViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    loadCart()
                } else {
                    _state.value = CartViewState.NotLoggedIn
                }
            }
        }
    }

    fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> {
                viewModelScope.launch { loadCart() }
            }
            is CartIntent.AddToCart -> {
                viewModelScope.launch { addToCart(intent.product, intent.quantity) }
            }
            is CartIntent.UpdateQuantity -> {
                viewModelScope.launch { updateQuantity(intent.cartItemId, intent.quantity) }
            }
            is CartIntent.RemoveItem -> {
                viewModelScope.launch { removeItem(intent.cartItemId) }
            }
            is CartIntent.ClearCart -> {
                viewModelScope.launch { clearCart() }
            }
            is CartIntent.Checkout -> {
                viewModelScope.launch { checkout(intent.shippingAddress) }
            }
        }
    }

    private suspend fun checkUserLoggedIn(): Boolean {
        return profileRepository.isLoggedIn.first()
    }

    private suspend fun addToCart(product: com.example.emtyapp.data.Entities.Product, quantity: Int = 1) {
        try {
            if (!checkUserLoggedIn()) {
                _state.value = CartViewState.NotLoggedIn
                return
            }

            cartRepository.addToCart(product, quantity)
            loadCartData()
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors de l'ajout au panier: ${e.message}")
        }
    }

    private suspend fun loadCart() {
        if (!checkUserLoggedIn()) {
            _state.value = CartViewState.NotLoggedIn
            return
        }

        try {
            _state.value = CartViewState.Loading

            (cartRepository as? com.example.emtyapp.data.Repository.CartRepositoryImpl)?.loadCartFromApi()

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
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors du chargement: ${e.message}")
        }
    }

    private suspend fun loadCartData() {
        try {
            val items = cartRepository.getCartItems().first()
            if (items.isEmpty()) {
                _state.value = CartViewState.EmptyCart
            } else {
                val total = cartRepository.getCartTotal()
                val count = cartRepository.getCartItemsCount()
                _state.value = CartViewState.CartLoaded(items, total, count)
            }
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors du chargement: ${e.message}")
        }
    }

    private suspend fun updateQuantity(cartItemId: String, quantity: Int) {
        try {
            if (!checkUserLoggedIn()) {
                _state.value = CartViewState.NotLoggedIn
                return
            }

            cartRepository.updateQuantity(cartItemId, quantity)
            loadCartData()
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors de la mise à jour: ${e.message}")
        }
    }

    private suspend fun removeItem(cartItemId: String) {
        try {
            if (!checkUserLoggedIn()) {
                _state.value = CartViewState.NotLoggedIn
                return
            }

            cartRepository.removeFromCart(cartItemId)
            loadCartData()
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors de la suppression: ${e.message}")
        }
    }

    private suspend fun clearCart() {
        try {
            if (!checkUserLoggedIn()) {
                _state.value = CartViewState.NotLoggedIn
                return
            }

            cartRepository.clearCart()
            _state.value = CartViewState.EmptyCart
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors de la suppression du panier: ${e.message}")
        }
    }

    private suspend fun checkout(shippingAddress: String) {
        try {
            if (!checkUserLoggedIn()) {
                _state.value = CartViewState.NotLoggedIn
                return
            }

            // Validation de l'adresse
            if (shippingAddress.trim().isEmpty()) {
                _state.value = CartViewState.Error("L'adresse de livraison est obligatoire")
                return
            }

            if (shippingAddress.trim().length < 10) {
                _state.value = CartViewState.Error("L'adresse de livraison doit être plus détaillée")
                return
            }

            _state.value = CartViewState.Loading

            val order = cartRepository.checkout(shippingAddress.trim())
            _state.value = CartViewState.CheckoutSuccess
        } catch (e: Exception) {
            _state.value = CartViewState.Error("Erreur lors de la commande: ${e.message}")
        }
    }
}