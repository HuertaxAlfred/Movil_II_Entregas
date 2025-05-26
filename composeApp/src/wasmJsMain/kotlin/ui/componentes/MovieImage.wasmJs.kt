package ui.componentes

//import org.jetbrains.compose.web.dom.Img
//import org.jetbrains.compose.web.dom.Img
//import org.jetbrains.compose.web.dom.Img
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

@Composable
actual fun MovieImage(url: String, modifier: Modifier, title: String) {
    val containerId = remember(url) { "image-container-${url.hashCode()}" }

    DisposableEffect(url, title) {
        val imagesRoot = document.getElementById("images-container")
            ?: error("No se encontró #images-container en el DOM")

        var container = document.getElementById(containerId) as? HTMLElement
        if (container == null) {
            container = document.createElement("div") as HTMLElement
            container.id = containerId

            container.style.width = "120px"     // Ancho más pequeño
            container.style.margin = "8px"
            container.style.display = "inline-block"
            container.style.verticalAlign = "top"
            container.style.textAlign = "center"
            container.style.boxSizing = "border-box"
            container.style.cursor = "pointer"
            // Puedes agregar sombra o borde para tarjeta si quieres
            container.style.border = "1px solid #ccc"
            container.style.borderRadius = "8px"
            container.style.padding = "8px"
            container.style.backgroundColor = "#fff"
            container.style.boxShadow = "0 2px 8px rgba(0,0,0,0.1)"

            imagesRoot.appendChild(container)
        }

        // Imagen
        var img = document.getElementById("${containerId}-img") as? HTMLElement
        if (img == null) {
            img = document.createElement("img") as HTMLElement
            img.id = "${containerId}-img"
            img.style.width = "100%"        // Que ocupe todo el ancho del contenedor
            img.style.height = "auto"       // Altura proporcional
            img.style.objectFit = "cover"
            img.style.borderRadius = "6px"
            container.appendChild(img)
        }
        img.setAttribute("src", url)

        // Título
        var titleDiv = document.getElementById("${containerId}-title") as? HTMLElement
        if (titleDiv == null) {
            titleDiv = document.createElement("div") as HTMLElement
            titleDiv.id = "${containerId}-title"
            titleDiv.style.marginTop = "8px"
            titleDiv.style.fontWeight = "bold"
            titleDiv.style.fontSize = "1rem"
            titleDiv.style.color = "#333"
            container.appendChild(titleDiv)
        }
        titleDiv.textContent = title

        onDispose {
            container.removeChild(img)
            container.removeChild(titleDiv)
            if (container.childElementCount == 0) {
                imagesRoot.removeChild(container)
            }
        }
    }
}
