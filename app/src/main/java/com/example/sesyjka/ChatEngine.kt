package com.example.sesyjka

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatEngine : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var mDbRef: DatabaseReference

    private var receiverUid: String? = null
    private var senderUid: String? = null

    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatengine)

        // Firebase init
        mDbRef = FirebaseDatabase.getInstance("https://sesyjkaapp-default-rtdb.europe-west1.firebasedatabase.app").getReference()


        val name = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser?.uid

        supportActionBar?.title = name

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        // Widoki
        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.send)

        // Adapter
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        // Odbieranie wiadomości
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("FIREBASE", "onDataChange wywołany, snapshot children count: ${snapshot.childrenCount}")

                    val newMessages = ArrayList<Message>()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            newMessages.add(message)
                        }
                    }

                    // Bezpieczna aktualizacja danych
                    messageList.clear()
                    messageList.addAll(newMessages)

                    // Powiadomienie adaptera na głównym wątku
                    messageRecyclerView.post {
                        messageAdapter.notifyDataSetChanged()
                        if (messageAdapter.itemCount > 0) {
                            messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FIREBASE", "DB Error: $error")
                }
            })
        if (senderUid == null || receiverUid == null) {
            finish()
            return
        }

        // Wysyłanie wiadomości
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            val messageObject = Message(messageText, senderUid ?: "")

            if (messageText.isNotEmpty()) {
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }

                messageBox.setText("")

                messageRecyclerView.post {
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0) {
                        messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }


    }
}
