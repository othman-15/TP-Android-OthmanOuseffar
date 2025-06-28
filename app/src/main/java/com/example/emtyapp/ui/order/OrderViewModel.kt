package com.example.emtyapp.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Repository.OrderRepository
import com.example.emtyapp.data.Repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    init {
        handleIntent(OrderIntent.LoadOrders)
    }

    fun handleIntent(intent: OrderIntent) {
        when (intent) {
            is OrderIntent.LoadOrders -> loadOrders()
            is OrderIntent.Refresh -> refreshOrders()
            is OrderIntent.CancelOrder -> cancelOrder(intent.orderId)
            is OrderIntent.ReorderItems -> reorderItems(intent.orderId)
            is OrderIntent.ClearError -> clearError()
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)

                val currentUser = profileRepository.currentUser.first()
                if (currentUser == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Vous devez être connecté pour voir vos commandes"
                    )
                    return@launch
                }

                val orders = orderRepository.getUserOrders(currentUser.id)
                _state.value = _state.value.copy(
                    isLoading = false,
                    orders = orders.sortedByDescending { it.date },
                    isEmpty = orders.isEmpty(),
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erreur lors du chargement des commandes"
                )
            }
        }
    }

    private fun refreshOrders() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isRefreshing = true, error = null)

                val currentUser = profileRepository.currentUser.first()
                if (currentUser == null) {
                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        error = "Vous devez être connecté pour voir vos commandes"
                    )
                    return@launch
                }

                val orders = orderRepository.getUserOrders(currentUser.id)
                _state.value = _state.value.copy(
                    isRefreshing = false,
                    orders = orders.sortedByDescending { it.date },
                    isEmpty = orders.isEmpty(),
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Erreur lors du rafraîchissement"
                )
            }
        }
    }

    private fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                val success = orderRepository.cancelOrder(orderId)
                if (success) {
                    // Recharger les commandes pour mettre à jour l'état
                    loadOrders()
                } else {
                    _state.value = _state.value.copy(
                        error = "Impossible d'annuler cette commande"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Erreur lors de l'annulation"
                )
            }
        }
    }

    private fun reorderItems(orderId: String) {
        viewModelScope.launch {
            try {
                val newOrder = orderRepository.reorderItems(orderId)
                // Recharger les commandes pour inclure la nouvelle commande
                loadOrders()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Erreur lors de la re-commande"
                )
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}