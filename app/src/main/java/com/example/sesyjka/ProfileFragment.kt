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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var spinnerDept: Spinner
    private lateinit var spinnerKierunek: Spinner
    private lateinit var spinnerRok: Spinner
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
        imgProfile     = view.findViewById(R.id.imgProfile)
        spinnerDept    = view.findViewById(R.id.spinnerDepartment)
        spinnerKierunek= view.findViewById(R.id.spinnerKierunek)
        spinnerRok     = view.findViewById(R.id.spinnerRok)
        etCity         = view.findViewById(R.id.etCity)
        etOpis         = view.findViewById(R.id.etOpis)
        btnSave        = view.findViewById(R.id.btnSave)

        // Spinner wydział
        spinnerDept.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            User.wydzialy
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Spinner kierunek
        spinnerKierunek.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            User.kierunki
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Spinner rok
        spinnerRok.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("I", "II", "III", "IV", "V", "VI", "VII")
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Obrazek profilowy
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedPhotoUri = it
                imgProfile.setImageURI(it)
            }
        }

        imgProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Wczytaj dane użytkownika
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

                // ustawienia spinnerów
                spinnerDept.setSelection(User.wydzialy.indexOf(user.wydzial).takeIf { it >= 0 } ?: 0)
                spinnerKierunek.setSelection(User.kierunki.indexOf(user.kierunek).takeIf { it >= 0 } ?: 0)
                spinnerRok.setSelection(
                    listOf("I", "II", "III", "IV", "V", "VI", "VII").indexOf(user.rok_studiow).takeIf { it >= 0 } ?: 0
                )

                // inne dane
                etCity.setText(user.miasto)
                etOpis.setText(user.opis)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Zapis profilu
        btnSave.setOnClickListener { saveProfile(uid) }

        return view
    }

    private fun saveProfile(uid: String) {
        val wydzial = spinnerDept.selectedItem as String
        val kierunek = spinnerKierunek.selectedItem as String
        val rok = spinnerRok.selectedItem as String
        val city = etCity.text.toString().trim()
        val opis = etOpis.text.toString().trim()

        if (wydzial.isBlank() || city.isBlank()) {
            Toast.makeText(requireContext(), "Wydział i miasto są wymagane", Toast.LENGTH_SHORT).show()
            return
        }

        val updateValues = mutableMapOf<String, Any>(
            "wydzial" to wydzial,
            "kierunek" to kierunek,
            "rok_studiow" to rok,
            "miasto" to city,
            "opis" to opis
        )

        val photoUri = selectedPhotoUri
        if (photoUri != null) {
            ImageUploader.uploadImageToSupabase(
                context = requireContext(),
                fileUri = photoUri,
                userId = uid,
                onSuccess = { publicUrl ->
                    updateValues["photoUrl"] = publicUrl
                    applyProfileUpdates(uid, updateValues)
                },
                onFailure = { errorMessage ->
                    Toast.makeText(requireContext(), "Błąd uploadu zdjęcia: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )
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
                Toast.makeText(
                    requireContext(),
                    "Błąd zapisu profilu: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
