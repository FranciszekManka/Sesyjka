package com.example.sesyjka

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import User
import kotlin.math.absoluteValue

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvOpis: TextView

    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        imgProfile = findViewById(R.id.imgProfile)
        tvName = findViewById(R.id.tvName)
        tvDepartment = findViewById(R.id.tvDepartment)
        tvCity = findViewById(R.id.tvCity)
        tvOpis = findViewById(R.id.tvOpis)

        val uid = intent.getStringExtra("user_uid")
        if (uid == null) {
            Toast.makeText(this, "Brak danych użytkownika", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mDbRef = FirebaseDatabase.getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")

        mDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    bindUser(user)
                } else {
                    Toast.makeText(this@UserProfileActivity, "Nie znaleziono użytkownika", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserProfileActivity, "Błąd ładowania danych", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun assignAvatarForUser(uid: String): Int {
        val avatarIndex = (uid.hashCode().absoluteValue % 8) + 1
        return resources.getIdentifier("avatar$avatarIndex", "drawable", packageName)
    }

    private fun bindUser(user: User) {
        tvName.text = user.name ?: "Brak imienia"
        tvDepartment.text = user.wydzial ?: "Brak wydziału"
        tvCity.text = user.miasto ?: "Brak miasta"
        tvOpis.text = user.opis ?: "Brak opisu"

        val avatarResId = assignAvatarForUser(user.uid)
        Glide.with(this)
            .load(avatarResId)
            .placeholder(R.drawable.ic_user_placeholder)
            .into(imgProfile)
    }
}
