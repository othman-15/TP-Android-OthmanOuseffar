package com.example.emtyapp.ui.product.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emtyapp.ui.profil.ProfileIntent
import com.example.emtyapp.ui.profil.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        if (state.error != null) {

            kotlinx.coroutines.delay(3000)
            viewModel.handleIntent(ProfileIntent.ClearError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoggedIn && state.currentUser != null) {

            UserProfileView(
                user = state.currentUser!!,
                onLogout = { viewModel.handleIntent(ProfileIntent.Logout) }
            )
        } else {

            AuthenticationView(
                state = state,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                onIntent = viewModel::handleIntent
            )
        }
    }
}

@Composable
private fun UserProfileView(
    user: com.example.emtyapp.data.Entities.User,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mon Profil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileInfoItem(label = "Nom", value = user.nom)
            ProfileInfoItem(label = "Prénom", value = user.prenom)
            ProfileInfoItem(label = "Email", value = user.email)
            ProfileInfoItem(label = "Téléphone", value = user.phone)
            ProfileInfoItem(label = "Adresse", value = user.address)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Se déconnecter")
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AuthenticationView(
    state: com.example.emtyapp.data.Entities.ProfileState,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onIntent: (ProfileIntent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.isLoginMode) "Connexion" else "Créer un compte",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoginMode) {
                LoginForm(
                    state = state,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                    onIntent = onIntent
                )
            } else {
                RegisterForm(
                    state = state,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                    onIntent = onIntent
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (state.isLoginMode) {
                        onIntent(ProfileIntent.Login)
                    } else {
                        onIntent(ProfileIntent.Register)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (state.isLoginMode) "Se connecter" else "Créer le compte")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onIntent(ProfileIntent.ToggleAuthMode) }
            ) {
                Text(
                    if (state.isLoginMode) {
                        "Pas de compte ? Créer un compte"
                    } else {
                        "Déjà un compte ? Se connecter"
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    state: com.example.emtyapp.data.Entities.ProfileState,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onIntent: (ProfileIntent) -> Unit
) {
    OutlinedTextField(
        value = state.loginEmail,
        onValueChange = { onIntent(ProfileIntent.UpdateLoginEmail(it)) },
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = state.showValidationErrors && state.loginEmail.isBlank()
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.loginPassword,
        onValueChange = { onIntent(ProfileIntent.UpdateLoginPassword(it)) },
        label = { Text("Mot de passe") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    if (passwordVisible) Icons.Default.Lock else Icons.Default.Star,
                    contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        isError = state.showValidationErrors && state.loginPassword.isBlank()
    )
}

@Composable
private fun RegisterForm(
    state: com.example.emtyapp.data.Entities.ProfileState,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onIntent: (ProfileIntent) -> Unit
) {
    OutlinedTextField(
        value = state.registerNom,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterNom(it)) },
        label = { Text("Nom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        isError = state.showValidationErrors && state.registerNom.isBlank()
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.registerPrenom,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterPrenom(it)) },
        label = { Text("Prénom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        isError = state.showValidationErrors && state.registerPrenom.isBlank()
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.registerEmail,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterEmail(it)) },
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = state.showValidationErrors && (state.registerEmail.isBlank() || !state.registerEmail.contains("@"))
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.registerPassword,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterPassword(it)) },
        label = { Text("Mot de passe") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    if (passwordVisible) Icons.Default.Lock else Icons.Default.Star,
                    contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        isError = state.showValidationErrors && state.registerPassword.length < 6,
        supportingText = if (state.showValidationErrors && state.registerPassword.isNotBlank() && state.registerPassword.length < 6) {
            { Text("Le mot de passe doit contenir au moins 6 caractères") }
        } else null
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.registerPhone,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterPhone(it)) },
        label = { Text("Téléphone") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        isError = state.showValidationErrors && state.registerPhone.isBlank()
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.registerAddress,
        onValueChange = { onIntent(ProfileIntent.UpdateRegisterAddress(it)) },
        label = { Text("Adresse") },
        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        isError = state.showValidationErrors && state.registerAddress.isBlank()
    )
}