package com.example.sesyjka
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class Login : ComponentActivity() {

    private lateinit var password_entry: EditText
    private lateinit var email_entry: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email_entry = findViewById(R.id.email_entry)
        password_entry = findViewById(R.id.password_entry)
        buttonLogin = findViewById((R.id.login_action))
        buttonSignup = findViewById(R.id.signin_action)

        buttonSignup.setOnClickListener {
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}

