package com.example.sesyjka
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.node.Ref
import com.example.sesyjka.ui.theme.SesyjkaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : ComponentActivity() {
    private lateinit var passwordEntry: EditText
    private lateinit var nameEntry: EditText
    private lateinit var emailEntry: EditText
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

        buttonSignup = findViewById(R.id.signin_action)
        buttonLogin = findViewById(R.id.login_action)


        buttonSignup.setOnClickListener {
            val name = nameEntry.text.toString()
            val email = emailEntry.text.toString()
            val password = passwordEntry.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                signUp(name, email, password)
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        buttonLogin.setOnClickListener {

            finish()
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
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
    private fun addUserToDatabase(name: String, email: String, uid: String) {

        val user = User(name, email, uid)


        mDbRef = FirebaseDatabase.getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("users")



        mDbRef.child(uid).setValue(user)
            .addOnSuccessListener {

                Log.d("Firebase", "Zapisano użytkownika do Realtime DB")
                Toast.makeText(this, "Użytkownik zapisany!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Log.e("Firebase", "Błąd zapisu: ${e.message}")
                Toast.makeText(this, "Błąd zapisu: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
