package io.github.shams66789.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.shams66789.contacts.databinding.ActivityDialBinding
import io.github.shams66789.contacts.mvvmarch.MainActivityViewModel
import io.github.shams66789.contacts.roomdb.entity.Contact

class DialActivity : AppCompatActivity(), PermissionCallback  {
    private val binding by lazy {
        ActivityDialBinding.inflate(layoutInflater)
    }

    var arrayList = ArrayList<Any>()
    var viewModel: MainActivityViewModel? = null
    var contactList = ArrayList<Contact>()
    lateinit var adapter : ContactAdapter
    lateinit var text : String
    private var positionOfPermissionRequest: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this@DialActivity).get(MainActivityViewModel::class.java)

        viewModel!!.data.observeForever{
            contactList.clear()
            it.map {
                contactList.add(it)
            }
            adapter.notifyDataSetChanged()
        }

        binding.rv1.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(contactList, this, this)
        binding.rv1.adapter = adapter

        binding.contactItem.setOnClickListener {
            startActivity(Intent(this, CreateContact::class.java)
                .putExtra("FLAG", 2)
                .putExtra("STRING", text))
        }

        binding.button10.setOnClickListener {
            arrayList.add("#")
            updateText()
        }

        binding.button8.setOnClickListener {
            arrayList.add(1)
            updateText()
        }

        binding.button14.setOnClickListener {
            arrayList.add(2)
            updateText()
        }

        binding.button15.setOnClickListener {
            arrayList.add(3)
            updateText()
        }

        binding.button11.setOnClickListener {
            arrayList.add(4)
            updateText()
        }

        binding.button9.setOnClickListener {
            arrayList.add(5)
            updateText()
        }

        binding.button13.setOnClickListener {
            arrayList.add(6)
            updateText()
        }

        binding.button12.setOnClickListener {
            arrayList.add(7)
            updateText()
        }

        binding.button6.setOnClickListener {
            arrayList.add(8)
            updateText()
        }

        binding.button4.setOnClickListener {
            arrayList.add(9)
            updateText()
        }

        binding.button3.setOnClickListener {
            arrayList.add(0)
            updateText()
        }

        binding.button5.setOnClickListener {
            arrayList.add("+")
            updateText()
        }

        binding.imageView3.setOnClickListener {
            if (arrayList.isNotEmpty()) {
                arrayList.removeAt(arrayList.size - 1)
                updateText()
            }
        }

        binding.imageButton4.setOnClickListener {
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter a Number before Calling", Toast.LENGTH_SHORT).show()
            } else {
                if (isCallPermissionGranted()) {
                    makePhoneCall()
                } else {
                    requestCallPermission(-1)
                }
            }
        }

    }

    private fun updateText() {
        text = arrayList.joinToString("")
        binding.editTextPhone.setText(text)
        filterContacts(text)
    }

    override fun isCallPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestCallPermission(position: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_PERMISSION_REQUEST_CODE
        )
        positionOfPermissionRequest = position
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (positionOfPermissionRequest == -1) {
                    makePhoneCall()
                }else {
                    makePhoneCall(positionOfPermissionRequest)
                }
            } else {
                Toast.makeText(this, "Permission denied. You can grant the permission in the app settings.",
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun makePhoneCall() {
        val num = text.toLong()
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$num")
        this.startActivity(intent)
    }

    private fun makePhoneCall(position: Int){
        if (position != -1) {
            val contact = contactList[position]
            val phoneNo = contact.phoneNo
            if (!phoneNo.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNo")
                this.startActivity(intent)
            } else {
                Toast.makeText(this, "Phone number not available for this contact.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error: Unable to determine the contact for the call.", Toast.LENGTH_SHORT).show()
        }
    }

    fun filterContacts(query: String?) {
        val tempList = if (query.isNullOrBlank()) {
            contactList

        } else {
            contactList.filter {
                it.phoneNo?.contains(query, ignoreCase = true) == true
            }
        }
        adapter.contactList = ArrayList(tempList)
        adapter.notifyDataSetChanged()

        if (tempList.isEmpty() && !query.isNullOrBlank()) {
            binding.secondView.visibility = View.VISIBLE
            binding.rv1.visibility = View.GONE
        } else {
            binding.secondView.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE
        }
    }
}