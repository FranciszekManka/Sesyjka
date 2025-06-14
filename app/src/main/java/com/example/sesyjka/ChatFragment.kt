package com.example.sesyjka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private val senderUid by lazy { mAuth.currentUser?.uid ?: "" }
    private val receiverUid = "ADMIN_UID" // <- Zmień na prawdziwe UID odbiorcy

    private val senderRoom by lazy { senderUid + receiverUid }
    private val receiverRoom by lazy { receiverUid + senderUid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_chatfragment, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app").reference

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageBox = view.findViewById(R.id.messagebox)
        sendButton = view.findViewById(R.id.send)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = messageAdapter

        // Pobieranie wiadomości z Firebase
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        // Wysyłanie wiadomości
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = Message(messageText, senderUid)
                mDbRef.child("chats").child(senderRoom).child("messages").push().setValue(message)
                mDbRef.child("chats").child(receiverRoom).child("messages").push().setValue(message)
                messageBox.setText("")
            }
        }

        return view
    }
}
