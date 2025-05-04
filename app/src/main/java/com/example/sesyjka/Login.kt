package com.example.sesyjka
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sesyjka.ui.theme.SesyjkaTheme
import com.google.firebase.auth.FirebaseAuth

class Login : ComponentActivity() {

    private lateinit var password_entry: EditText
    private lateinit var email_entry: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button

    private lateinit var maAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        maAuth = FirebaseAuth.getInstance()


        email_entry = findViewById(R.id.email_entry)
        password_entry = findViewById(R.id.password_entry)
        buttonLogin = findViewById((R.id.login_action))
        buttonSignup = findViewById(R.id.signin_action)


        buttonLogin.setOnClickListener {
            val email = email_entry.text.toString()
            val password = password_entry.toString()

        }

    }
    private fun login(email: String, password: String){
        maAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

