package com.secondhand.shop.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.google.firebase.firestore.FirebaseFirestore
import com.secondhand.shop.R
import com.secondhand.shop.model.User // Import your User model

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val ecoGreen = Color(0xFF4CAF50)
    val scrollState = rememberScrollState()

    // Firebase Instances
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.mipmap.second_hand_shop_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Text("Start your eco-friendly shopping journey", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            CustomOutlinedTextField(fullName, { fullName = it }, "Full Name", ecoGreen = ecoGreen)
            Spacer(modifier = Modifier.height(16.dp))
            CustomOutlinedTextField(email, { email = it }, "Email Address", KeyboardType.Email, ecoGreen = ecoGreen)
            Spacer(modifier = Modifier.height(16.dp))
            CustomOutlinedTextField(password, { password = it }, "Password", isPassword = true, isPasswordVisible = passwordVisible, onVisibilityToggle = { passwordVisible = !passwordVisible }, ecoGreen = ecoGreen)
            Spacer(modifier = Modifier.height(16.dp))
            CustomOutlinedTextField(confirmPassword, { confirmPassword = it }, "Confirm Password", isPassword = true, isPasswordVisible = confirmPasswordVisible, onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }, ecoGreen = ecoGreen)

            Spacer(modifier = Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = ecoGreen)
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                    onClick = {
                        // --- REGISTER LOGIC ---
                        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val uid = result.user?.uid ?: ""
                                // Create User Object using your Model
                                val newUser = User(
                                    uid = uid,
                                    fullName = fullName,
                                    email = email,
                                    profileImageUrl = "" // Default empty
                                )

                                // Save to Firestore
                                firestore.collection("users").document(uid)
                                    .set(newUser)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onRegisterSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Auth Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                ) {
                    Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = onNavigateBack, contentPadding = PaddingValues(0.dp)) {
                    Text("Login", color = ecoGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- Custom Outlined Text Field ---
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
        label = { Text(label) }, // Consistent with Login Screen
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        trailingIcon = {
            if (isPassword && onVisibilityToggle != null) {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = Color.Gray
                    )
                }
            }
        },
        // --- Updated color and border style to match Login Screen ---
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ecoGreen,
            unfocusedBorderColor = Color(0xFF7E7E7E),
            focusedLabelColor = ecoGreen,
            unfocusedLabelColor = Color.Gray,
            cursorColor = ecoGreen,
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    )
}