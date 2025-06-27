package com.example.emtyapp.data.Entities

data class ProfileState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val isLoginMode: Boolean = true,
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerNom: String = "",
    val registerPrenom: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerPhone: String = "",
    val registerAddress: String = "",
    val showValidationErrors: Boolean = false
)