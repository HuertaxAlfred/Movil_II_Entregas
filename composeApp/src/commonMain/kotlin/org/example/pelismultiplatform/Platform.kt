package org.example.pelismultiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform