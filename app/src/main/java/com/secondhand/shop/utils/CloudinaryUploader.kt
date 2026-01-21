package com.secondhand.shop.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object CloudinaryUploader {

    // These values match your screenshots exactly
    private const val CLOUD_NAME = "ddxtjfv6r"
    private const val UPLOAD_PRESET = "secondhand_shop"

    suspend fun uploadBitmap(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        uploadToCloudinary(stream.toByteArray())
    }

    suspend fun uploadFile(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        bytes?.let { uploadToCloudinary(it) }
    }

    private suspend fun uploadToCloudinary(bytes: ByteArray): String? = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        // 1. Create the request body
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "upload.jpg",
                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        // 2. Build the request pointing to Cloudinary's API
        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: ""
                val jsonResponse = JSONObject(bodyString)

                // This captures the real HTTPS link from Cloudinary's response
                jsonResponse.getString("secure_url")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}