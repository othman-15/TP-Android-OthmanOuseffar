package com.example.emtyapp.ui.product.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
            ) {
                ProfileHeader()
            }

            Spacer(modifier = Modifier.height(32.dp))


            AnimatedContent(
                targetState = state.isLoggedIn && state.currentUser != null,
                transitionSpec = {
                    slideInHorizontally { if (targetState) it else -it } + fadeIn() togetherWith
                            slideOutHorizontally { if (targetState) -it else it } + fadeOut()
                },
                label = "profile_content"
            ) { isLoggedIn ->
                if (isLoggedIn && state.currentUser != null) {
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mon Profil",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Gérez vos informations personnelles",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UserProfileView(
    user: com.example.emtyapp.data.Entities.User,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bienvenue ${user.prenom}! ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Votre profil est complet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informations du profil
            ProfileInfoCard(
                items = listOf(
                    ProfileInfo("Nom", user.nom, Icons.Default.Person),
                    ProfileInfo("Prénom", user.prenom, Icons.Default.Person),
                    ProfileInfo("Email", user.email, Icons.Default.Email),
                    ProfileInfo("Téléphone", user.phone, Icons.Default.Phone),
                    ProfileInfo("Adresse", user.address, Icons.Default.Home)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Se déconnecter",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(items: List<ProfileInfo>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            items.forEachIndexed { index, item ->
                ProfileInfoItem(
                    icon = item.icon,
                    label = item.label,
                    value = item.value
                )
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode toggle avec animation
            AuthModeToggle(
                isLoginMode = state.isLoginMode,
                onToggle = { onIntent(ProfileIntent.ToggleAuthMode) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Formulaires avec animation
            AnimatedContent(
                targetState = state.isLoginMode,
                transitionSpec = {
                    slideInHorizontally { if (targetState) -it else it } + fadeIn() togetherWith
                            slideOutHorizontally { if (targetState) it else -it } + fadeOut()
                },
                label = "auth_form"
            ) { isLoginMode ->
                if (isLoginMode) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Affichage des erreurs
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                if (state.error != null) {
                    ErrorCard(message = state.error)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (state.isLoginMode) {
                        onIntent(ProfileIntent.Login)
                    } else {
                        onIntent(ProfileIntent.Register)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        if (state.isLoginMode) Icons.Default.AccountBox else Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.isLoginMode) "Se connecter" else "Créer le compte",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthModeToggle(
    isLoginMode: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            ToggleButton(
                text = "Connexion",
                icon = Icons.Default.AccountBox,
                isSelected = isLoginMode,
                onClick = { if (!isLoginMode) onToggle() },
                modifier = Modifier.weight(1f)
            )
            ToggleButton(
                text = "Inscription",
                icon = Icons.Default.Person,
                isSelected = !isLoginMode,
                onClick = { if (isLoginMode) onToggle() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(2.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                }
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
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
    Column {
        StyledTextField(
            value = state.loginEmail,
            onValueChange = { onIntent(ProfileIntent.UpdateLoginEmail(it)) },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = state.showValidationErrors && state.loginEmail.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledTextField(
            value = state.loginPassword,
            onValueChange = { onIntent(ProfileIntent.UpdateLoginPassword(it)) },
            label = "Mot de passe",
            icon = Icons.Default.Lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = onPasswordVisibilityToggle,
            isError = state.showValidationErrors && state.loginPassword.isBlank()
        )
    }
}

@Composable
private fun RegisterForm(
    state: com.example.emtyapp.data.Entities.ProfileState,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onIntent: (ProfileIntent) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyledTextField(
                value = state.registerNom,
                onValueChange = { onIntent(ProfileIntent.UpdateRegisterNom(it)) },
                label = "Nom",
                icon = Icons.Default.Person,
                isError = state.showValidationErrors && state.registerNom.isBlank(),
                modifier = Modifier.weight(1f)
            )

            StyledTextField(
                value = state.registerPrenom,
                onValueChange = { onIntent(ProfileIntent.UpdateRegisterPrenom(it)) },
                label = "Prénom",
                icon = Icons.Default.Person,
                isError = state.showValidationErrors && state.registerPrenom.isBlank(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        StyledTextField(
            value = state.registerEmail,
            onValueChange = { onIntent(ProfileIntent.UpdateRegisterEmail(it)) },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = state.showValidationErrors && (state.registerEmail.isBlank() || !state.registerEmail.contains("@"))
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledTextField(
            value = state.registerPassword,
            onValueChange = { onIntent(ProfileIntent.UpdateRegisterPassword(it)) },
            label = "Mot de passe",
            icon = Icons.Default.Lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = onPasswordVisibilityToggle,
            isError = state.showValidationErrors && state.registerPassword.length < 6,
            supportingText = if (state.showValidationErrors && state.registerPassword.isNotBlank() && state.registerPassword.length < 6) {
                "Le mot de passe doit contenir au moins 6 caractères"
            } else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledTextField(
            value = state.registerPhone,
            onValueChange = { onIntent(ProfileIntent.UpdateRegisterPhone(it)) },
            label = "Téléphone",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            isError = state.showValidationErrors && state.registerPhone.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledTextField(
            value = state.registerAddress,
            onValueChange = { onIntent(ProfileIntent.UpdateRegisterAddress(it)) },
            label = "Adresse",
            icon = Icons.Default.Home,
            isError = state.showValidationErrors && state.registerAddress.isBlank()
        )
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        },
        trailingIcon = if (isPassword && onPasswordVisibilityToggle != null) {
            {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        if (passwordVisible) Icons.Default.Lock else Icons.Default.CheckCircle,
                        contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    )
}

data class ProfileInfo(
    val label: String,
    val value: String,
    val icon: ImageVector
)