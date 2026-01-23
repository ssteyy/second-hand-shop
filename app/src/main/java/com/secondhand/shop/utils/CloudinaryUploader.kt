package com.secondhand.shop.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object CloudinaryUploader {

    // 🔐 Cloudinary config (Unsigned upload)
    private const val CLOUD_NAME = "ddxtjfv6r"
    private const val UPLOAD_PRESET = "secondhand_shop"

    private val client = OkHttpClient()

    /**
     * Upload Bitmap (Camera)
     */
    suspend fun uploadBitmap(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        uploadToCloudinary(stream.toByteArray())
    }

    /**
     * Upload Image from Gallery (Uri)
     */
    suspend fun uploadFile(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes()
        }
        bytes?.let { uploadToCloudinary(it) }
    }

    suspend fun uploadProfileImage(context: Context, bitmap: Bitmap?, uri: Uri?): String? {
        return when {
            bitmap != null -> {
                CloudinaryUploader.uploadBitmap(bitmap)
            }
            uri != null -> {
                CloudinaryUploader.uploadFile(context, uri)
            }
            else -> null
        }
    }

    /**
     * Core upload logic
     */
    private suspend fun uploadToCloudinary(bytes: ByteArray): String? =
        withContext(Dispatchers.IO) {

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "profile.jpg",
                    bytes.toRequestBody("image/jpeg".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null

                    val body = response.body?.string() ?: return@withContext null
                    val json = JSONObject(body)

                    // ✅ Always use secure_url
                    json.getString("secure_url")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
