package com.example.sesyjka

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.UUID

object SupabaseConfig {
    const val SUPABASE_URL = "https://aqkuspbceibdoyzbkgrh.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFxa3VzcGJjZWliZG95emJrZ3JoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk5MTgwMjksImV4cCI6MjA2NTQ5NDAyOX0.aT30qGK99ocfsW6I9sMbPfgc1mFy0xYQi4TWVaoeWEk"
}

object ImageUploader {

    private val client = OkHttpClient()

    fun uploadImageToSupabase(
        context: Context,
        fileUri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val filePath = getRealPathFromURI(fileUri, context)
        if (filePath.isBlank()) {
            onFailure("Nie udało się odczytać ścieżki pliku")
            return
        }

        val file = File(filePath)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        val bucket = "profile-photos"
        val fileName = "users/${UUID.randomUUID()}.jpg"

        val supabaseUrl = SupabaseConfig.SUPABASE_URL
        val anonKey = SupabaseConfig.SUPABASE_ANON_KEY

        val uploadRequest = Request.Builder()
            .url("$supabaseUrl/storage/v1/object/$bucket/$fileName")
            .addHeader("apikey", anonKey)
            .addHeader("Authorization", "Bearer $anonKey")
            .put(requestBody)
            .build()

        client.newCall(uploadRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure("Błąd uploadu: ${e.message ?: "nieznany błąd"}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure("Błąd HTTP uploadu: ${response.code}")
                    return
                }
                val publicUrl = "$supabaseUrl/storage/v1/object/public/$bucket/$fileName"

                updateUserPhotoUrl(supabaseUrl, anonKey, userId, publicUrl, onSuccess, onFailure)
            }
        })
    }

    private fun updateUserPhotoUrl(
        supabaseUrl: String,
        anonKey: String,
        userId: String,
        photoUrl: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("photo_url", photoUrl)

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        val updateRequest = Request.Builder()
            .url("$supabaseUrl/rest/v1/users?id=eq.$userId")
            .addHeader("apikey", anonKey)
            .addHeader("Authorization", "Bearer $anonKey")
            .addHeader("Content-Type", "application/json")
            .method("PATCH", requestBody)
            .build()

        client.newCall(updateRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure("Błąd zapisu URL w DB: ${e.message ?: "nieznany błąd"}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess(photoUrl)
                } else {
                    onFailure("Błąd HTTP zapisu URL w DB: ${response.code}")
                }
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri, context: Context): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        } ?: uri.path ?: ""
    }
}
