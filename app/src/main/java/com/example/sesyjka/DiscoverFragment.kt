package com.example.sesyjka

import User
import android.content.Intent
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
import kotlin.math.absoluteValue
import kotlin.random.Random

class DiscoverFragment : Fragment() {

    private lateinit var container: FrameLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var userList = mutableListOf<User>()
    private var currentIndex = 0
    private val avatarMap = mutableMapOf<String, Int>()

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

        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                avatarMap.clear()
                for (child in snapshot.children) {
                    val u = child.getValue(User::class.java)
                    if (u != null && u.uid != mAuth.currentUser?.uid) {
                        userList.add(u)
                        avatarMap[u.uid!!] = Random.nextInt(1, 9)
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

    private fun getAvatarResource(uid: String): Int {
        val avatarIndex = avatarMap[uid] ?: 1
        return resources.getIdentifier(
            "avatar$avatarIndex",
            "drawable",
            requireContext().packageName
        )
    }

    private fun showNextCard() {
        container.removeAllViews()
        if (currentIndex >= userList.size) {
            Toast.makeText(context, "Brak więcej użytkowników", Toast.LENGTH_SHORT).show()
            return
        }

        val user = userList[currentIndex++]
        val card = layoutInflater.inflate(R.layout.swipe_card_item, container, false) as CardView

        // Uzupełnienie danych na karcie
        Glide.with(this)
            .load(getAvatarResource(user.uid ?: ""))
            .placeholder(R.drawable.ic_user_placeholder)
            .into(card.findViewById<ImageView>(R.id.imgProfile))

        card.findViewById<TextView>(R.id.tvName).text = user.name ?: "Brak imienia"
        card.findViewById<TextView>(R.id.tvAge).text = user.age?.toString() ?: "?"
        card.findViewById<TextView>(R.id.tvDepartment).text = user.wydzial ?: ""
        card.findViewById<TextView>(R.id.tvCity).text = user.miasto ?: ""
        card.findViewById<TextView>(R.id.tvKierunek).text = user.kierunek ?: ""
        card.findViewById<TextView>(R.id.tvRok).text = user.rok_studiow ?: ""
        card.findViewById<TextView>(R.id.tvOpis).text = user.opis ?: ""

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
                    val dy = v.translationY
                    val thresholdX = v.width / 4
                    val thresholdY = v.height / 4

                    when {
                        dx > thresholdX -> {
                            Toast.makeText(context, "Spotkanie!", Toast.LENGTH_SHORT).show()
                            openChat(user)
                            animateOut(v, v.width * 2f)
                        }
                        dx < -thresholdX -> {
                            Toast.makeText(context, "Odrzucono", Toast.LENGTH_SHORT).show()
                            animateOut(v, -v.width * 2f)
                        }
                        dy < -thresholdY -> {
                            Toast.makeText(context, "Piwko 🍺?", Toast.LENGTH_SHORT).show()
                            openChat(user)
                            animateOut(v, 0f, -v.height * 2f)
                        }
                        dy > thresholdY -> {
                            Toast.makeText(context, "Otwieram profil...", Toast.LENGTH_SHORT).show()
                            openUserProfile(user)
                            resetCard(v)
                        }
                        else -> resetCard(v)
                    }
                    true
                }
                else -> false
            }
        }

        container.addView(card)
    }

    private fun animateOut(v: View, toX: Float, toY: Float = 0f) {
        v.animate()
            .translationX(toX)
            .translationY(toY)
            .alpha(0f)
            .setDuration(200)
            .withEndAction { showNextCard() }
            .start()
    }

    private fun resetCard(v: View) {
        v.animate()
            .translationX(0f)
            .translationY(0f)
            .rotation(0f)
            .setDuration(200)
            .start()
    }

    private fun openChat(user: User) {
        val intent = Intent(requireContext(), ChatEngine::class.java)
        intent.putExtra("uid", user.uid)
        intent.putExtra("name", user.name)
        intent.putExtra("age", user.age)
        requireActivity().startActivity(intent)
    }

    private fun openUserProfile(user: User) {
        val intent = Intent(requireContext(), UserProfileActivity::class.java)
        intent.putExtra("user_uid", user.uid)
        requireActivity().startActivity(intent)
    }
}
