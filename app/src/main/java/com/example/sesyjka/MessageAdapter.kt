package com.example.sesyjka

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_SENT = 2
    val ITEM_RECIEVE = 1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if(viewType == 1){
            //recieve layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_recieve, parent, false)
            return recieveViewHolder(view)
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
            val viewHolder = holder as recieveViewHolder
            viewHolder.recieveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECIEVE
        }

    }
    override fun getItemCount(): Int {
        return messageList.size

    }

    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.message_sent)
    }
    class recieveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recieveMessage = itemView.findViewById<TextView>(R.id.message_recieve)
    }


}