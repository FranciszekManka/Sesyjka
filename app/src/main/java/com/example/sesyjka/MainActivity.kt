package com.example.sesyjka

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesyjka.ui.theme.SesyjkaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserExtendRecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDbRef = FirebaseDatabase.getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app").getReference()
        mAuth = FirebaseAuth.getInstance()
        userList = ArrayList()
        adapter = UserExtendRecyclerView(this, userList)
        supportActionBar?.hide()


        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)

        userRecyclerView.adapter = adapter

        mDbRef.child("users").addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    Log.d("FIREBASE_USER", "User: $currentUser")
                    Log.d("FIREBASE_USER", "UserListSize: ${userList.size}, Example: ${userList.firstOrNull()}")

                    if (currentUser != null) {
                        userList.add(currentUser)
                    }
                }
                Toast.makeText(this@MainActivity, "Pobrano: ${userList.size} użytkowników", Toast.LENGTH_SHORT).show()

                userRecyclerView.post {
                    Toast.makeText(this@MainActivity, "Pierwszy użytkownik: ${userList.firstOrNull()?.name}", Toast.LENGTH_LONG).show()
                }

                adapter.notifyDataSetChanged()

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout) {
            mAuth.signOut()
            finish()
            return true
        }
        return true
    }

}


