package com.example.sesyjka

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_SENT = 2
    val ITEM_RECEIVE = 1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if(viewType == 1){
            //recieve layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_receive, parent, false)
            return receiveViewHolder(view)
        }else{
            //sent layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false)
            return sentViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val currentMessage  = messageList[position]
        if (holder.javaClass == sentViewHolder::class.java) {
            val viewHolder = holder as sentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as receiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val viewType = if (currentUserUid == currentMessage.senderId) ITEM_SENT else ITEM_RECEIVE

        Log.d("ADAPTER_VIEWTYPE", "Position: $position, message: '${currentMessage.message}', senderId: ${currentMessage.senderId}, currentUserUid: $currentUserUid, viewType: $viewType")

        return viewType
    }



    override fun getItemCount(): Int = messageList.size


    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.message_sent)
    }
    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.message_receive)
    }


}