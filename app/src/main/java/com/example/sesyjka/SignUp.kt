package com.example.sesyjka

import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : ComponentActivity() {
    private lateinit var passwordEntry: EditText
    private lateinit var nameEntry: EditText
    private lateinit var emailEntry: EditText
    private lateinit var ageEntry: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()

        nameEntry = findViewById(R.id.name_entry)
        emailEntry = findViewById(R.id.email_entry)
        passwordEntry = findViewById(R.id.password_entry)
        ageEntry = findViewById(R.id.age_entry)
        buttonSignup = findViewById(R.id.signin_action)
        buttonLogin = findViewById(R.id.login_action)

        buttonSignup.setOnClickListener {
            val name = nameEntry.text.toString().trim()
            val email = emailEntry.text.toString().trim()
            val password = passwordEntry.text.toString()
            val ageText = ageEntry.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank() || ageText.isBlank()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 18 || age > 120) {
                Toast.makeText(this, "Podaj poprawny wiek (18–120 lat)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUp(name, email, password, age)
        }

        buttonLogin.setOnClickListener {
            finish()
        }
    }

    private fun signUp(name: String, email: String, password: String, age: Int) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid!!
                    addUserToDatabase(name, email, uid, age)
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, age: Int) {
        val user = User(name, email, uid, age)

        mDbRef = FirebaseDatabase
            .getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")

        mDbRef.child(uid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Użytkownik zapisany!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignUp, CompleteProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Błąd zapisu: ${e.message}")
                Toast.makeText(this, "Błąd zapisu: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
