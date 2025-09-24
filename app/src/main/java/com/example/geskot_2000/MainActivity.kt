package com.example.geskot_2000

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.geskot_2000.ui.theme.GesKot2000Theme
import java.io.BufferedReader
import java.io.InputStreamReader

data class Estacion(
    val direccion: String,
    val bicisDisponibles: Int,
    val espaciosLibres: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GesKot2000Theme {
                val estaciones = remember { leerCSV() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaEstaciones(
                        estaciones = estaciones,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    private fun leerCSV(fileName: String = "valenbisi.csv"): List<Estacion> {
        val estaciones = mutableListOf<Estacion>()
        val inputStream = assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.readLine()

        reader.forEachLine { line ->
            val tokens = line.split(";")
            if (tokens.size >= 3) {
                val estacion = Estacion(
                    direccion = tokens[0],
                    bicisDisponibles = tokens[1].toIntOrNull() ?: 0,
                    espaciosLibres = tokens[2].toIntOrNull() ?: 0
                )
                estaciones.add(estacion)
            }
        }

        reader.close()
        return estaciones
    }
}

// Composable para mostrar la lista
@Composable
fun ListaEstaciones(
    estaciones: List<Estacion>,
    modifier: Modifier = Modifier   // <--- agregamos este parámetro
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(estaciones) { estacion ->
            val color = when {
                estacion.bicisDisponibles == 0 -> Color.Red
                estacion.bicisDisponibles < 5 -> Color.Yellow
                else -> Color.Green
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Dirección: ${estacion.direccion}", color = Color.White)
                    Text("Bicis disponibles: ${estacion.bicisDisponibles}", color = Color.White)
                    Text("Espacios libres: ${estacion.espaciosLibres}", color = Color.White)
                }
            }
        }
    }
}

