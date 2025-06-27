package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.LoginRequest
import com.example.emtyapp.data.Entities.RegisterRequest
import com.example.emtyapp.data.Entities.User
import com.example.emtyapp.data.api.ApiService
import com.example.emtyapp.data.preferences.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val users = apiService.getUsers()
            if (users.isSuccessful) {
                val user = users.body()?.find {
                    it.email == email && it.password == password
                }
                if (user != null) {

                    userPreferences.saveUserData(user)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Email ou mot de passe incorrect"))
                }
            } else {
                Result.failure(Exception("Erreur de connexion au serveur"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Result<User> {
        return try {

            val existingUsers = apiService.getUsers()
            if (existingUsers.isSuccessful) {
                val emailExists = existingUsers.body()?.any { it.email == registerRequest.email } == true
                if (emailExists) {
                    return Result.failure(Exception("Un compte avec cet email existe déjà"))
                }
            }


            val newUser = User(
                nom = registerRequest.nom,
                prenom = registerRequest.prenom,
                email = registerRequest.email,
                password = registerRequest.password,
                phone = registerRequest.phone,
                address = registerRequest.address
            )

            val response = apiService.registerUser(newUser)
            if (response.isSuccessful && response.body() != null) {

                userPreferences.saveUserData(response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la création du compte"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreferences.clearUserData()
    }


    val isLoggedIn = userPreferences.isLoggedIn
    val currentUser = userPreferences.userData
}