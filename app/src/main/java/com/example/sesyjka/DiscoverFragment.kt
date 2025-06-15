package com.example.sesyjka

import User
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DiscoverFragment : Fragment() {

    private lateinit var container: FrameLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var userList = mutableListOf<User>()
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        containerParent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.discover_fragment, containerParent, false)
        container = view.findViewById(R.id.card_container)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase
            .getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")

        // Pobierz i wymieszaj
        mDbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (child in snapshot.children) {
                    val u = child.getValue(User::class.java)
                    if (u != null && u.uid != mAuth.currentUser?.uid) {
                        userList.add(u)
                    }
                }
                userList.shuffle()
                showNextCard()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Błąd ładowania", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    private fun showNextCard() {
        container.removeAllViews()
        if (currentIndex >= userList.size) {
            Toast.makeText(context, "Brak więcej użytkowników", Toast.LENGTH_SHORT).show()
            return
        }
        val user = userList[currentIndex++]
        // Inflate karty
        val card = layoutInflater.inflate(R.layout.swipe_card_item, container, false) as CardView

        // Wypełnij dane
        val img = card.findViewById<ImageView>(R.id.imgProfile)
        Glide.with(this).load(user.photoUrl).placeholder(R.drawable.ic_user_placeholder).into(img)
        card.findViewById<TextView>(R.id.tvName).text = user.name ?: "Brak imienia"
        card.findViewById<TextView>(R.id.tvDepartment).text = user.wydzial
        card.findViewById<TextView>(R.id.tvCity).text = user.miasto
        card.findViewById<TextView>(R.id.tvOpis).text = user.opis

        // Touch listener do swipe
        var downX = 0f
        var downY = 0f
        card.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY
                    v.translationX = dx
                    v.translationY = dy
                    v.rotation = dx / 20f
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val dx = v.translationX
                    val threshold = v.width / 4
                    if (dx > threshold) {
                        // Swipe w prawo
                        v.animate()
                            .translationX(v.width * 2f)
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction { showNextCard() }
                            .start()
                    } else if (dx < -threshold) {
                        // Swipe w lewo
                        v.animate()
                            .translationX(-v.width * 2f)
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction { showNextCard() }
                            .start()
                    } else {
                        // Cofnij do środka
                        v.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .rotation(0f)
                            .setDuration(200)
                            .start()
                    }
                    true
                }
                else -> false
            }
        }

        container.addView(card)
    }
}
