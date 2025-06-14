package com.example.sesyjka

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var spinnerDept: Spinner
    private lateinit var etCity: EditText
    private lateinit var etOpis: EditText
    private lateinit var btnSave: Button

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedPhotoUri: Uri? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        // Firebase
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance(
            "https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("users")

        // Widoki
        imgProfile   = view.findViewById(R.id.imgProfile)
        spinnerDept  = view.findViewById(R.id.spinnerDepartment)
        etCity       = view.findViewById(R.id.etCity)
        etOpis       = view.findViewById(R.id.etOpis)
        btnSave      = view.findViewById(R.id.btnSave)

        // 1) Spinner z wydziałami
        val wydzialy = User.wydzialy
        spinnerDept.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            wydzialy
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // 2) Picker obrazów
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedPhotoUri = it
                imgProfile.setImageURI(it)
            }
        }
        imgProfile.setOnClickListener { pickImageLauncher.launch("image/*") }

        // 3) Wczytaj istniejące dane użytkownika
        val uid = mAuth.currentUser!!.uid
        mDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java) ?: return
                // zdjęcie
                if (user.photoUrl.isNotBlank()) {
                    Glide.with(this@ProfileFragment)
                        .load(user.photoUrl)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(imgProfile)
                }
                // wydział
                val pos = wydzialy.indexOf(user.wydzial)
                if (pos >= 0) spinnerDept.setSelection(pos)
                // miasto i opis
                etCity.setText(user.miasto)
                etOpis.setText(user.opis)
            }

            override fun onCancelled(error: DatabaseError) { /* opcjonalnie obsłuż */ }
        })

        // 4) Zapis zmian
        btnSave.setOnClickListener { saveProfile(uid) }

        return view
    }

    private fun saveProfile(uid: String) {
        val dept = spinnerDept.selectedItem as String
        val city = etCity.text.toString().trim()
        val opis = etOpis.text.toString().trim()

        if (dept.isBlank() || city.isBlank()) {
            Toast.makeText(requireContext(), "Wydział i miasto są wymagane", Toast.LENGTH_SHORT).show()
            return
        }

        // jeśli wybrano nowe zdjęcie, najpierw upload:
        val updateValues = mutableMapOf<String, Any>(
            "wydzial" to dept,
            "miasto"  to city,
            "opis"    to opis
        )

        val uploadTask: Task<Uri>? = selectedPhotoUri?.let { uri ->
            val ref = FirebaseStorage.getInstance().getReference("/profile_images/$uid")
            ref.putFile(uri)
                .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                    if (!task.isSuccessful) task.exception?.let { throw it }
                    ref.downloadUrl
                }
        }

        if (uploadTask != null) {
            uploadTask.addOnSuccessListener { downloadUri ->
                updateValues["photoUrl"] = downloadUri.toString()
                applyProfileUpdates(uid, updateValues)
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(),
                    "Błąd uploadu zdjęcia: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {

            applyProfileUpdates(uid, updateValues)
        }
    }

    private fun applyProfileUpdates(uid: String, updates: Map<String, Any>) {
        mDbRef.child(uid).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profil zaktualizowany", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(),
                    "Błąd zapisu profilu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
