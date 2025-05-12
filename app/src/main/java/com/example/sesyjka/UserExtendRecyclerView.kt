package com.example.sesyjka

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserExtendRecyclerView(val context: Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserExtendRecyclerView.UserViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val currentUser = userList[position]
        Log.d("USER_LIST", "User: ${currentUser.name}")
        holder.textName.text = currentUser.name
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatEngine::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }

}