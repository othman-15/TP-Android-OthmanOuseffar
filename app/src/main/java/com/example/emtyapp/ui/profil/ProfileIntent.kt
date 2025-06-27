package com.example.emtyapp.ui.profil

sealed class ProfileIntent {
    object ToggleAuthMode : ProfileIntent()
    object Login : ProfileIntent()
    object Register : ProfileIntent()
    object Logout : ProfileIntent()
    object ClearError : ProfileIntent()

    data class UpdateLoginEmail(val email: String) : ProfileIntent()
    data class UpdateLoginPassword(val password: String) : ProfileIntent()

    data class UpdateRegisterNom(val nom: String) : ProfileIntent()
    data class UpdateRegisterPrenom(val prenom: String) : ProfileIntent()
    data class UpdateRegisterEmail(val email: String) : ProfileIntent()
    data class UpdateRegisterPassword(val password: String) : ProfileIntent()
    data class UpdateRegisterPhone(val phone: String) : ProfileIntent()
    data class UpdateRegisterAddress(val address: String) : ProfileIntent()
}