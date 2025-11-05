package com.example.appvidasalud.ui.theme.view

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appvidasalud.ui.theme.GreenPrimary
import com.example.appvidasalud.ui.theme.TextWhite
import com.example.appvidasalud.viewmodel.FoodViewModel
import com.example.appvidasalud.viewmodel.FoodViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFoodScreen(
    navController: NavController,
    viewModel: FoodViewModel = viewModel(
        factory = FoodViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    // --- Estado de la UI ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Esta URI se genera ANTES de tomar la foto
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    // Esto nos dice si la foto se tomó exitosamente
    var hasPhoto by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // --- Lógica para crear la URI de la imagen ---
    fun getTempImageUri(context: Context): Uri {
        // Usa el FileProvider definido en el Manifest
        val authority = "${context.packageName}.provider"
        val tempFile = File.createTempFile("pic_${System.currentTimeMillis()}_", ".jpg", context.cacheDir).apply {
            createNewFile()
        }
        return FileProvider.getUriForFile(context, authority, tempFile)
    }

    // --- Lanzador de Cámara ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // El resultado (success) nos dice si el usuario guardó la foto o canceló
            hasPhoto = success
            if (!success) {
                // Si el usuario cancela, borramos la URI para que no se guarde nada
                imageUri = null
            }
        }
    )

    // --- Lanzador de Permisos ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permiso CONCEDIDO: creamos una nueva URI y lanzamos la cámara
                val newImageUri = getTempImageUri(context)
                imageUri = newImageUri // Guardamos la URI ANTES de lanzar la cámara
                cameraLauncher.launch(newImageUri)
            } else {
                // Opcional: Mostrar un SnackBar o mensaje al usuario
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Registro", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Para que no se corte en pantallas chicas
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Botón para tomar la foto
            Button(onClick = {
                // Primero pedimos permiso de cámara
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasPhoto) "Tomar otra foto" else "Tomar Foto (+)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // La imagen solo es visible si la foto se tomó (hasPhoto = true)
            AnimatedVisibility(visible = hasPhoto && imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Comida registrada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la comida") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (calorías, ingredientes, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // "Publicar": Guardamos en la BD y volvemos a la lista
                    viewModel.saveFoodEntry(title, description, imageUri.toString())
                    navController.popBackStack() // Vuelve a FoodLogScreen
                },
                // El botón solo se activa si hay foto y hay un título
                enabled = hasPhoto && imageUri != null && title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Publicar Registro")
            }
        }
    }
}