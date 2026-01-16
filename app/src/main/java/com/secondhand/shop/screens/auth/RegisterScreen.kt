package com.secondhand.shop.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secondhand.shop.R

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // --- State Management ---
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Visibility states for password fields
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val ecoGreen = Color(0xFF4CAF50)

    // Firebase instances
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // --- Header Section ---
        Image(
            painter = painterResource(id = R.mipmap.second_hand_shop_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Text(
            text = "Start your eco-friendly shopping journey",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Input Fields ---
        CustomOutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            ecoGreen = ecoGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email,
            ecoGreen = ecoGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field with Eye Toggle
        CustomOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            ecoGreen = ecoGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field with Eye Toggle
        CustomOutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isPassword = true,
            isPasswordVisible = confirmPasswordVisible,
            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            ecoGreen = ecoGreen
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Register Button & Logic ---
        if (isLoading) {
            CircularProgressIndicator(color = ecoGreen)
        } else {
            Button(
                onClick = {
                    // Basic Validation
                    if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    // 1. Create user in Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid ?: ""
                                val user = hashMapOf(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "uid" to uid,
                                    "createdAt" to System.currentTimeMillis()
                                )
                                // 2. Save info in Firestore
                                db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onRegisterSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Auth Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen)
            ) {
                Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Footer Link ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onNavigateBack) {
                Text("Login", color = ecoGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null,
    ecoGreen: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        // Logic to switch between hidden dots and actual text
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        trailingIcon = {
            if (isPassword && onVisibilityToggle != null) {
                val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (isPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = onVisibilityToggle) {
                    Icon(imageVector = icon, contentDescription = description, tint = Color.Gray)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ecoGreen,
            focusedLabelColor = ecoGreen,
            cursorColor = ecoGreen
        )
    )
}