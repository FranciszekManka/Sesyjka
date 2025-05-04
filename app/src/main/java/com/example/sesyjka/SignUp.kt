package com.example.sesyjka

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sesyjka.ui.theme.SesyjkaTheme
import com.google.firebase.auth.FirebaseAuth

class SignUp : ComponentActivity() {
    private lateinit var passwordEntry: EditText
    private lateinit var emailEntry: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button
    private lateinit var maAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        maAuth = FirebaseAuth.getInstance()


        emailEntry = findViewById(R.id.email_entry)
        passwordEntry = findViewById(R.id.password_entry)

        buttonSignup = findViewById(R.id.signin_action)


        buttonSignup.setOnClickListener {
            val email = emailEntry.text.toString()
            val password = passwordEntry.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                signUp(email, password)
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }


        buttonLogin.setOnClickListener {

            finish()
        }
    }

    private fun signUp(email: String, password: String) {
        maAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
