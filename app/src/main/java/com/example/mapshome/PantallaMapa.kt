package com.example.mapshome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.android.gms.maps.CameraUpdateFactory


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PantallaMapa(viewModel: MapaViewModel = viewModel()) {

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    if (locationPermissionState.status.isGranted) {
        MapContent(context, viewModel)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Se requiere permiso de ubicación PRECISO para continuar.")
        }
    }
}
@SuppressLint("MissingPermission")
@Composable
fun MapContent(context: Context, viewModel: MapaViewModel) {
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var ubicacionActual by remember { mutableStateOf<LatLng?>(null) }
    var tipoMapaSeleccionado by remember { mutableStateOf(MapType.NORMAL) }
    var expanded by remember { mutableStateOf(false) }

    val posicionCamara = rememberCameraPositionState()

    // Obtener ubicación actual
    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                ubicacionActual = LatLng(it.latitude, it.longitude)
                viewModel.setUbicacionActual(it.latitude, it.longitude)
            }
        }
    }

    // Centrar cámara en la ubicación actual
    LaunchedEffect(ubicacionActual) {
        ubicacionActual?.let {
            posicionCamara.animate(
                CameraUpdateFactory.newLatLngZoom(it, 16f),
                durationMs = 1000
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .padding(top = 50.dp),
            cameraPositionState = posicionCamara,
            properties = MapProperties(
                isMyLocationEnabled = true,
                isBuildingEnabled = true,
                isIndoorEnabled = true,
                isTrafficEnabled = true,
                mapType = tipoMapaSeleccionado
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true
            )
        ) {
            ubicacionActual?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Ubicación Actual"
                )
            }

            viewModel.destinoCoordendas?.let { home ->
                Marker(
                    state = MarkerState(position = home),
                    title = "Destino"
                )
            }
            viewModel.puntosDeLaRuta?.let { points ->
                Polyline(
                    points = points,
                    color = Color.Magenta,
                    width = 7f
                )
            }

        }
    }

    // Controles de interfaz
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = viewModel.destinoString,
            onValueChange = viewModel::actualizarTextoDelTxtDestino,
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val homeAddress = viewModel.destinoString.trim()

                    if (homeAddress.isEmpty()) {
                        Toast.makeText(context, "Por favor ingresa una ubicación", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.obtenerCoordenadasSegunTxtDestino(context, homeAddress) { latLng ->
                            val message = if (latLng != null) {
                                viewModel.actualizarDestinoCoordenadas(latLng)  // <- Actualiza en el ViewModel
                                "Ubicación encontrada"
                            } else {
                                "No se pudo encontrar la ubicación"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ubicar")
            }


            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.obtenerRuta() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mostrar RUTA")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                Button(onClick = { expanded = true }) {
                    Text("Tipo de mapa")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Normal") },
                        onClick = {
                            tipoMapaSeleccionado = MapType.NORMAL
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Satélite") },
                        onClick = {
                            tipoMapaSeleccionado = MapType.SATELLITE
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Terreno") },
                        onClick = {
                            tipoMapaSeleccionado = MapType.TERRAIN
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Híbrido") },
                        onClick = {
                            tipoMapaSeleccionado = MapType.HYBRID
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}