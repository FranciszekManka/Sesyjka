package com.example.sesyjka

import User
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatList: ArrayList<User>
    private lateinit var adapter: UserExtendRecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_chatfragment, container, false)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatList = ArrayList()
        adapter = UserExtendRecyclerView(requireContext(), chatList)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance(
            "https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("chats")

        loadChatUsers()
        return view
    }

    private fun loadChatUsers() {
        val currentUid = auth.currentUser?.uid ?: return

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (chatSnapshot in snapshot.children) {
                    if (chatSnapshot.key?.contains(currentUid) == true) {
                        val otherUid = chatSnapshot.key!!.replace(currentUid, "")
                        // Pobieramy dane użytkownika
                        FirebaseDatabase.getInstance(
                            "https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app"
                        ).getReference("users").child(otherUid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    if (user != null && !chatList.contains(user)) {
                                        chatList.add(user)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }
                if (chatList.isEmpty()) {
                    Toast.makeText(context, "Brak aktywnych czatów", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}
