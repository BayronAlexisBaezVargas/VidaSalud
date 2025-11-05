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
import com.example.appvidasalud.ui.theme.GreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferencesRepository(context) }

    // Estados inicializados vacíos, se cargarán desde DataStore
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar datos guardados al iniciar
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Cabecera Verde ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tu Perfil", style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aquí podrás ver y actualizar tu información", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)))
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
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFE0E0E0)).padding(20.dp).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
                }
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(GreenPrimary).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Edición rápida de nombre
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                if (isEditingName) {
                    OutlinedTextField(
                        value = tempName, onValueChange = { tempName = it }, singleLine = true, modifier = Modifier.weight(1f, false),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                    )
                    IconButton(onClick = {
                        userName = tempName
                        isEditingName = false
                        // Guardado rápido solo del nombre
                        scope.launch { userPrefs.updateUserProfile(userName, userEmail, userPhone, null) }
                    }) { Icon(Icons.Default.Check, null, tint = GreenPrimary) }
                } else {
                    Text(userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = { tempName = userName; isEditingName = true }) { Icon(Icons.Default.Edit, null) }
                }
            }
            Text(userEmail.ifBlank { "Sin email registrado" }, color = Color.Gray, fontSize = 14.sp)
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-30).dp), thickness = 2.dp, color = GreenPrimary.copy(alpha = 0.3f))

        // --- Sección Acceso y Seguridad ---
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-10).dp)) {
            Text("Acceso y Seguridad", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(value = userEmail, onValueChange = { userEmail = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, focusedLabelColor = GreenPrimary, cursorColor = GreenPrimary))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = userPhone, onValueChange = { userPhone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, focusedLabelColor = GreenPrimary, cursorColor = GreenPrimary))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword, onValueChange = { newPassword = it }, label = { Text("Nueva Contraseña") }, placeholder = { Text("Dejar en blanco para mantener actual") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, focusedLabelColor = GreenPrimary, cursorColor = GreenPrimary)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { showSaveDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Save, null, tint = Color.White); Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0E0)), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFD32F2F)); Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Diálogo de Confirmación con LÓGICA REAL ---
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            icon = { Icon(Icons.Default.Save, null, tint = GreenPrimary) },
            title = { Text("Confirmar cambios") },
            text = { Text(if (newPassword.isNotBlank()) "¿Estás seguro de que deseas actualizar tu información y CAMBIAR TU CONTRASEÑA?" else "¿Deseas guardar los cambios en tu perfil?") },
            confirmButton = {
                TextButton(onClick = {
                    // --- AQUÍ ESTÁ LA MAGIA: GUARDADO REAL ---
                    scope.launch {
                        userPrefs.updateUserProfile(userName, userEmail, userPhone, newPassword.ifBlank { null })
                        newPassword = "" // Limpiamos el campo por seguridad
                        showSaveDialog = false
                        // Opcional: Mostrar un Snackbar confirmando el guardado
                    }
                }) { Text("Confirmar", color = GreenPrimary, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Cancelar", color = Color.Gray) } },
            containerColor = Color.White
        )
    }
}