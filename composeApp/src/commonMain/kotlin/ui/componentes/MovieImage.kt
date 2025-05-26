package ui.componentes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MovieImage(url: String, modifier: Modifier = Modifier, title: String = "")

