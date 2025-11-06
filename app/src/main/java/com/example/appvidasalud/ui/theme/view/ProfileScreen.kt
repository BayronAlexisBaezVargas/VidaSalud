package com.example.appvidasalud.ui.theme.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appvidasalud.data.UserPreferencesRepository
// No importes GreenPrimary, usa el tema
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferencesRepository(context) }

    // Estados
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar datos
    LaunchedEffect(Unit) {
        userPrefs.userProfileFlow.collect { profile ->
            userName = profile.name
            userEmail = profile.email
            userPhone = profile.phone
        }
    }

    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) profileImageUri = uri }
    )

    // Colores del tema
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // <-- CAMBIO
            .verticalScroll(rememberScrollState())
    ) {
        // --- Cabecera Verde ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(primaryColor), // <-- CAMBIO
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tu Perfil", style = MaterialTheme.typography.headlineMedium.copy(color = onPrimaryColor, fontWeight = FontWeight.Bold)) // <-- CAMBIO
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aquí podrás ver y actualizar tu información", style = MaterialTheme.typography.bodyMedium.copy(color = onPrimaryColor.copy(alpha = 0.9f))) // <-- CAMBIO
            }
        }

        // --- Sección Foto y Nombre ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-50).dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri, contentDescription = null,
                        modifier = Modifier.size(120.dp).clip(CircleShape).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, tint = onSurfaceVariantColor, // <-- CAMBIO
                        modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).padding(20.dp).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) // <-- CAMBIO
                }
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(primaryColor).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, contentAlignment = Alignment.Center) { // <-- CAMBIO
                    Icon(Icons.Default.Edit, null, tint = onPrimaryColor, modifier = Modifier.size(18.dp)) // <-- CAMBIO
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Edición rápida de nombre
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                if (isEditingName) {
                    OutlinedTextField(
                        value = tempName, onValueChange = { tempName = it }, singleLine = true, modifier = Modifier.weight(1f, false),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, cursorColor = primaryColor) // <-- CAMBIO
                    )
                    IconButton(onClick = {
                        userName = tempName
                        isEditingName = false
                        scope.launch { userPrefs.updateUserProfile(userName, userEmail, userPhone, null) }
                    }) { Icon(Icons.Default.Check, null, tint = primaryColor) } // <-- CAMBIO
                } else {
                    Text(userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = onBackgroundColor)) // <-- CAMBIO
                    IconButton(onClick = { tempName = userName; isEditingName = true }) { Icon(Icons.Default.Edit, null, tint = onBackgroundColor) } // <-- CAMBIO
                }
            }
            Text(userEmail.ifBlank { "Sin email registrado" }, color = onSurfaceVariantColor, fontSize = 14.sp) // <-- CAMBIO
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-30).dp), thickness = 2.dp, color = primaryColor.copy(alpha = 0.3f)) // <-- CAMBIO

        // --- Sección Acceso y Seguridad ---
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-10).dp)) {
            Text("Acceso y Seguridad", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = onBackgroundColor), modifier = Modifier.padding(bottom = 16.dp)) // <-- CAMBIO

            val textFieldColors = OutlinedTextFieldDefaults.colors( // <-- Definición de colores para TextFields
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                cursorColor = primaryColor
            )

            OutlinedTextField(value = userEmail, onValueChange = { userEmail = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = textFieldColors) // <-- CAMBIO
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = userPhone, onValueChange = { userPhone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = textFieldColors) // <-- CAMBIO
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword, onValueChange = { newPassword = it }, label = { Text("Nueva Contraseña") }, placeholder = { Text("Dejar en blanco para mantener actual") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = textFieldColors // <-- CAMBIO
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { showSaveDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor), shape = RoundedCornerShape(12.dp)) { // <-- CAMBIO
                Icon(Icons.Default.Save, null, tint = onPrimaryColor); Spacer(modifier = Modifier.width(8.dp)) // <-- CAMBIO
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onPrimaryColor) // <-- CAMBIO
            }
            Spacer(modifier = Modifier.height(40.dp))

            val errorContainerColor = MaterialTheme.colorScheme.errorContainer
            val onErrorContainerColor = MaterialTheme.colorScheme.onErrorContainer

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = errorContainerColor), shape = RoundedCornerShape(12.dp)) { // <-- CAMBIO
                Icon(Icons.Default.ExitToApp, null, tint = onErrorContainerColor); Spacer(modifier = Modifier.width(8.dp)) // <-- CAMBIO
                Text("Cerrar Sesión", color = onErrorContainerColor, fontWeight = FontWeight.Bold, fontSize = 16.sp) // <-- CAMBIO
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Diálogo de Confirmación con LÓGICA REAL ---
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            icon = { Icon(Icons.Default.Save, null, tint = primaryColor) }, // <-- CAMBIO
            title = { Text("Confirmar cambios") },
            text = { Text(if (newPassword.isNotBlank()) "¿Estás seguro de que deseas actualizar tu información y CAMBIAR TU CONTRASEÑA?" else "¿Deseas guardar los cambios en tu perfil?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        userPrefs.updateUserProfile(userName, userEmail, userPhone, newPassword.ifBlank { null })
                        newPassword = ""
                        showSaveDialog = false
                    }
                }) { Text("Confirmar", color = primaryColor, fontWeight = FontWeight.Bold) } // <-- CAMBIO
            },
            dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Cancelar", color = onSurfaceVariantColor) } }, // <-- CAMBIO
            containerColor = MaterialTheme.colorScheme.surface // <-- CAMBIO
        )
    }
}