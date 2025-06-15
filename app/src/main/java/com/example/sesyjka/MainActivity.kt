package com.example.sesyjka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        replaceFragment(DiscoverFragment())


        bottomNav.selectedItemId = R.id.nav_match


        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_chat -> replaceFragment(ChatFragment())
                R.id.nav_match -> replaceFragment(DiscoverFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
