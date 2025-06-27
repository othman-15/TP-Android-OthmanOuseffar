package com.example.emtyapp.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.Entities.ProfileState
import com.example.emtyapp.data.Entities.RegisterRequest
import com.example.emtyapp.data.Repository.ProfileRepository
import com.example.emtyapp.data.Repository.CartRepository
import com.example.emtyapp.data.Repository.CartRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {

        viewModelScope.launch {
            combine(
                profileRepository.isLoggedIn,
                profileRepository.currentUser
            ) { isLoggedIn, user ->
                val previouslyLoggedIn = _state.value.isLoggedIn

                _state.value = _state.value.copy(
                    isLoggedIn = isLoggedIn,
                    currentUser = user
                )


                if (isLoggedIn && !previouslyLoggedIn && user != null) {
                    loadUserCart()
                }

                else if (!isLoggedIn && previouslyLoggedIn) {
                    clearLocalCart()
                }
            }.collect { }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.ToggleAuthMode -> toggleAuthMode()
            is ProfileIntent.Login -> login()
            is ProfileIntent.Register -> register()
            is ProfileIntent.Logout -> logout()
            is ProfileIntent.ClearError -> clearError()
            is ProfileIntent.UpdateLoginEmail -> updateLoginEmail(intent.email)
            is ProfileIntent.UpdateLoginPassword -> updateLoginPassword(intent.password)
            is ProfileIntent.UpdateRegisterNom -> updateRegisterNom(intent.nom)
            is ProfileIntent.UpdateRegisterPrenom -> updateRegisterPrenom(intent.prenom)
            is ProfileIntent.UpdateRegisterEmail -> updateRegisterEmail(intent.email)
            is ProfileIntent.UpdateRegisterPassword -> updateRegisterPassword(intent.password)
            is ProfileIntent.UpdateRegisterPhone -> updateRegisterPhone(intent.phone)
            is ProfileIntent.UpdateRegisterAddress -> updateRegisterAddress(intent.address)
        }
    }

    private fun toggleAuthMode() {
        _state.value = _state.value.copy(
            isLoginMode = !_state.value.isLoginMode,
            error = null,
            showValidationErrors = false
        )
    }

    private fun login() {
        val currentState = _state.value

        if (currentState.loginEmail.isBlank() || currentState.loginPassword.isBlank()) {
            _state.value = currentState.copy(
                error = "Veuillez remplir tous les champs",
                showValidationErrors = true
            )
            return
        }

        _state.value = currentState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            profileRepository.login(currentState.loginEmail, currentState.loginPassword)
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null,

                        loginEmail = "",
                        loginPassword = ""
                    )

                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Erreur de connexion"
                    )
                }
        }
    }

    private fun register() {
        val currentState = _state.value

        if (!isRegisterFormValid(currentState)) {
            _state.value = currentState.copy(
                error = "Veuillez remplir tous les champs correctement",
                showValidationErrors = true
            )
            return
        }

        _state.value = currentState.copy(isLoading = true, error = null)

        val registerRequest = RegisterRequest(
            nom = currentState.registerNom,
            prenom = currentState.registerPrenom,
            email = currentState.registerEmail,
            password = currentState.registerPassword,
            phone = currentState.registerPhone,
            address = currentState.registerAddress
        )

        viewModelScope.launch {
            profileRepository.register(registerRequest)
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null,

                        registerNom = "",
                        registerPrenom = "",
                        registerEmail = "",
                        registerPassword = "",
                        registerPhone = "",
                        registerAddress = ""
                    )

                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Erreur lors de la création du compte"
                    )
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            profileRepository.logout()

            _state.value = ProfileState()
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun updateLoginEmail(email: String) {
        _state.value = _state.value.copy(loginEmail = email)
    }

    private fun updateLoginPassword(password: String) {
        _state.value = _state.value.copy(loginPassword = password)
    }

    private fun updateRegisterNom(nom: String) {
        _state.value = _state.value.copy(registerNom = nom)
    }

    private fun updateRegisterPrenom(prenom: String) {
        _state.value = _state.value.copy(registerPrenom = prenom)
    }

    private fun updateRegisterEmail(email: String) {
        _state.value = _state.value.copy(registerEmail = email)
    }

    private fun updateRegisterPassword(password: String) {
        _state.value = _state.value.copy(registerPassword = password)
    }

    private fun updateRegisterPhone(phone: String) {
        _state.value = _state.value.copy(registerPhone = phone)
    }

    private fun updateRegisterAddress(address: String) {
        _state.value = _state.value.copy(registerAddress = address)
    }

    private fun isRegisterFormValid(state: ProfileState): Boolean {
        return state.registerNom.isNotBlank() &&
                state.registerPrenom.isNotBlank() &&
                state.registerEmail.isNotBlank() &&
                state.registerEmail.contains("@") &&
                state.registerPassword.isNotBlank() &&
                state.registerPassword.length >= 6 &&
                state.registerPhone.isNotBlank() &&
                state.registerAddress.isNotBlank()
    }

    // Charger le panier de l'utilisateur connecté
    private fun loadUserCart() {
        viewModelScope.launch {
            try {
                (cartRepository as? CartRepositoryImpl)?.loadCartFromApi()
            } catch (e: Exception) {

            }
        }
    }


    private fun clearLocalCart() {
        viewModelScope.launch {
            try {
                (cartRepository as? CartRepositoryImpl)?.clearLocalCart()
            } catch (e: Exception) {

            }
        }
    }
}