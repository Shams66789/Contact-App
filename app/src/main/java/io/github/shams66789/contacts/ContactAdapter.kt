package io.github.shams66789.contacts

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.github.shams66789.contacts.databinding.ContactItemsBinding
import io.github.shams66789.contacts.roomdb.entity.Contact

interface PermissionCallback {
    fun isCallPermissionGranted(): Boolean
    fun requestCallPermission(position: Int)
}

class ContactAdapter(var contactList: List<Contact>, var context : Context, var permissionCallback: PermissionCallback) : RecyclerView
    .Adapter<ContactAdapter.MyViewHolder>() {

    var callingNo : Long? = 0

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
//        holder.binding.email.text = contact.email

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, CreateContact::class.java)
                .putExtra("FLAG", 1)
                .putExtra("DATA", contact))
        }

        holder.binding.call.setOnClickListener {
            callingNo = contactList[position].phoneNo?.toLong()
            if (permissionCallback.isCallPermissionGranted()) {
                makePhoneCall()
            } else {
                // Request permission through the callback
                permissionCallback.requestCallPermission(position)
            }
        }

        holder.binding.signImage.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.image_dialog)

            val image = dialog.findViewById<ImageView>(R.id.imageView2)
            val imageObject = holder.binding.signImage.drawable
            image.setImageDrawable(imageObject)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val lp = WindowManager.LayoutParams()
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                lp.blurBehindRadius = 100
                lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                dialog.window?.attributes = lp
            }
            dialog.show()
        }
    }

    fun makePhoneCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$callingNo")
        context.startActivity(intent)
    }
}
