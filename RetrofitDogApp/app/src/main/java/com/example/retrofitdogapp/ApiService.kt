package com.example.retrofitdogapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
/*
Obtiene imágenes de perros por raza
@GET indica que es una solicitud HTTP GET
 @Path se utiliza para agregar el valor de la variable raza a la URL
 */

@GET("{raza}/images")
fun getDogsByBreed(@Path("raza") raza: String?): Call<DogsResponse?>?
}
