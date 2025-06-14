package com.example.sesyjka

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var spinnerDept: Spinner
    private lateinit var etCity: EditText
    private lateinit var btnSave: Button
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedPhotoUri: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        // Firebase
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance(
            "https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("users")

        // Widoki
        imgProfile    = findViewById(R.id.imgProfile)
        spinnerDept   = findViewById(R.id.spinnerDepartment)
        etCity        = findViewById(R.id.etCity)
        btnSave       = findViewById(R.id.btnSave)

        // Spinner z wydziałami
        val wydzialy = User.wydzialy
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            wydzialy
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerDept.adapter = adapter

        // Picker obrazów
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                selectedPhotoUri = it
                imgProfile.setImageURI(it)
            }
        }
        imgProfile.setOnClickListener { pickImageLauncher.launch("image/*") }

        // Wczytaj istniejące dane (edycja)
        val uid = mAuth.currentUser!!.uid
        mDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java) ?: return
                val pos = wydzialy.indexOf(user.wydzial)
                if (pos >= 0) spinnerDept.setSelection(pos)
                etCity.setText(user.miasto)
                // TODO: wczytać photoUrl przez Glide
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Zapis profilu
        btnSave.setOnClickListener { saveProfile() }
    }

    private fun saveProfile() {
        val uid = mAuth.currentUser!!.uid
        val dept = spinnerDept.selectedItem as String
        val city = etCity.text.toString().trim()
        if (dept.isBlank() || city.isBlank() || selectedPhotoUri == null) {
            Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Upload zdjęcia do Storage
        val storageRef = FirebaseStorage.getInstance().getReference("/profile_images/$uid")
        storageRef.putFile(selectedPhotoUri!!)
            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                if (!task.isSuccessful) {
                    // rzuć wyjątek, jeśli upload nie przeszedł
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri: Uri ->
                // 2) Zapis danych w Realtime DB
                val updates = mapOf<String, Any>(
                    "department" to dept,
                    "city"       to city,
                    "photoUrl"   to downloadUri.toString()
                )
                mDbRef.child(uid).updateChildren(updates)
                    .addOnSuccessListener {
                        startActivity(
                            Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        finish()
                    }
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(
                    this,
                    "Błąd uploadu: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
