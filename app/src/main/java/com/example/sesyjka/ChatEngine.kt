package com.example.sesyjka

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sesyjka.ui.theme.SesyjkaTheme

class ChatEngine : AppCompatActivity() {

    private lateinit var messageRecyclerView: UserExtendRecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        val intent = Intent()
        val name = intent.getStringExtra("name")
        val uid = intent.getStringExtra("uid")

        supportActionBar?.title = name

        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.send)
    }
}



























