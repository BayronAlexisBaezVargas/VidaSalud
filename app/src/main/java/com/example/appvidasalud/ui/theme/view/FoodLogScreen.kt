package com.example.appvidasalud.ui.theme.view

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage // Importante para cargar la imagen
import com.example.appvidasalud.data.food.FoodEntry
import com.example.appvidasalud.ui.theme.GreenPrimary
import com.example.appvidasalud.ui.theme.TextWhite
import com.example.appvidasalud.viewmodel.FoodViewModel
import com.example.appvidasalud.viewmodel.FoodViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(
    navController: NavController,
    viewModel: FoodViewModel = viewModel(
        factory = FoodViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    // Recolectamos la lista de comidas desde el ViewModel
    val foodList by viewModel.allEntries.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Registro de Comidas", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        },
        floatingActionButton = {
            // Este es el botón "+" que mencionaste
            FloatingActionButton(
                onClick = {
                    // Navega a la pantalla de tomar la foto
                    navController.navigate("register_food")
                },
                containerColor = GreenPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar comida", tint = TextWhite)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (foodList.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has registrado ninguna comida. ¡Presiona el botón '+' para empezar!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                // Renderiza cada item de la lista
                items(foodList) { entry ->
                    FoodEntryItem(entry = entry)
                }
            }
        }
    }
}

/**
 * Composable para mostrar un solo item de la lista de comidas.
 */
@Composable
fun FoodEntryItem(entry: FoodEntry) {
    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy, HH:mm 'hrs'", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen (usando Coil)
            AsyncImage(
                model = Uri.parse(entry.imageUri), // Convertimos el String de la BD a Uri
                contentDescription = entry.title,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Contenido (Título, Desc, Fecha)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(entry.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateFormatter.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}