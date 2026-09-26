package com.example.retrofitdogapp

import com.google.gson.annotations.SerializedName

// Definición de la clase DogsResponse que representa la respuesta de la API
class DogsResponse {

    // Campo que representa el estado de la respuesta de la API
    @SerializedName("status")
    private var status: String? = null

    // Campo que representa la lista de URLs de imágenes
    @SerializedName("message")
    private var images: List<String?>? = null
    // Obtiene el estado de la respuesta
    fun getStatus(): String? {
        return status
    }
    // Establece el estado de la respuesta
    fun setStatus(status: String?) {
        this.status = status
    }
    // Obtiene la lista de URLs de imágenes
    fun getImages(): List<String?>? {
        return images
    }
    // Establece la lista de URLs de imágenes
    fun setImages(images: List<String?>?) {
        this.images = images
    }
}
