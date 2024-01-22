package io.github.shams66789.contacts

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.shams66789.contacts.databinding.ContactItemsBinding
import io.github.shams66789.contacts.roomdb.entity.Contact


class ContactAdapter(var contactList: List<Contact>, var context : Context) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    inner class MyViewHolder(var binding: ContactItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ContactItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactList[position]

        if (contact.profile != null) {
            val imageByte = contact.profile
            if (imageByte != null) {
                val image = BitmapFactory.decodeByteArray(imageByte,0,imageByte.size)
                holder.binding.signImage.setImageBitmap(image)
                holder.binding.signImage.visibility = View.VISIBLE
                holder.binding.signText.visibility = View.GONE
            } else {
                val splitName = contact.name?.split(" ")
                var sign : String = " "
                splitName?.forEach {
                    if (it.isNotEmpty()) {
                        sign += it[0]
                    }
                }
                holder.binding.signText.text =sign
                holder.binding.signImage.visibility = View.GONE
                holder.binding.signText.visibility = View.VISIBLE
            }
        } else {
            val splitName = contact.name?.split(" ")
            var sign : String = " "
            splitName?.forEach {
                if (it.isNotEmpty()) {
                    sign += it[0]
                }
            }

            holder.binding.signText.text = sign
        }

        holder.binding.name.text = contact.name
        holder.binding.phone.text = contact.phoneNo
        holder.binding.email.text = contact.email
    }

}