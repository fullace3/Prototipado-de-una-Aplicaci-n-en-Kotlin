package com.example.geskot_2000

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.geskot_2000.ui.theme.GesKot2000Theme
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ValenbisiResponse(
    val records: List<Record>
)

data class Record(
    val fields: Fields
)

data class Fields(
    val address: String?,
    val available: Int?,
    val free: Int?
)

data class Estacion(
    val direccion: String,
    val bicisDisponibles: Int,
    val espaciosLibres: Int
)

interface ValenbisiApi {
    @GET("api/records/1.0/search/")
    fun getStations(
        @Query("dataset") dataset: String = "valenbisi-disponibilitat-valenbisi-dsiponibilidad",
        @Query("rows") rows: Int = 100
    ): Call<ValenbisiResponse>
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://valencia.opendatasoft.com/  ")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ValenbisiApi::class.java)

        setContent {
            GesKot2000Theme {
                var estaciones by remember { mutableStateOf<List<Estacion>>(emptyList()) }

                LaunchedEffect(Unit) {
                    api.getStations().enqueue(object : Callback<ValenbisiResponse> {
                        override fun onResponse(
                            call: Call<ValenbisiResponse>,
                            response: Response<ValenbisiResponse>
                        ) {
                            val datos = response.body()?.records?.map {
                                Estacion(
                                    direccion = it.fields.address ?: "Desconocida",
                                    bicisDisponibles = it.fields.available ?: 0,
                                    espaciosLibres = it.fields.free ?: 0
                                )
                            } ?: emptyList()
                            estaciones = datos
                        }

                        override fun onFailure(call: Call<ValenbisiResponse>, t: Throwable) {
                            Log.e("API_ERROR", "Error: ${t.message}")
                        }
                    })
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaEstaciones(
                        estaciones = estaciones,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun ListaEstaciones(estaciones: List<Estacion>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dirección",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Libres",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Bicis",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        items(estaciones) { estacion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = estacion.direccion,
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${estacion.espaciosLibres}",
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                val backgroundColor = when {
                    estacion.bicisDisponibles == 0 -> Color.Red
                    estacion.bicisDisponibles < 5 -> Color.Yellow
                    else -> Color.Green
                }
                val textColor = if (estacion.bicisDisponibles < 5 && estacion.bicisDisponibles > 0) Color.Black else Color.White

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${estacion.bicisDisponibles}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
