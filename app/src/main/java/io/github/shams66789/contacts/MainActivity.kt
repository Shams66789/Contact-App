package io.github.shams66789.contacts

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.shams66789.contacts.databinding.ActivityMainBinding
import io.github.shams66789.contacts.mvvmarch.MainActivityViewModel
import io.github.shams66789.contacts.roomdb.entity.Contact

class MainActivity : AppCompatActivity(), PermissionCallback {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    var viewModel: MainActivityViewModel? = null
    var contactList = ArrayList<Contact>()
    lateinit var adapter : ContactAdapter
    private var positionOfPermissionRequest: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        updateEmptyListVisibility()

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateContact::class.java))
        }

        binding.dialPad.setOnClickListener {
            startActivity(Intent(this@MainActivity,DialActivity::class.java))
        }

        viewModel!!.data.observeForever{
            contactList.clear()
            it.map {
                contactList.add(it)
            }
            adapter.notifyDataSetChanged()
            updateEmptyListVisibility()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            1,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                  direction: Int) {
                if (ItemTouchHelper.LEFT == direction) {
                    showDeleteConfirmationDialog(viewHolder.adapterPosition)
                }
            }

        }).attachToRecyclerView(binding.rv)

        binding.rv.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(contactList, this, this)
        binding.rv.adapter = adapter

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText)
                return true
            }
        })
    }

    fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)

        alertDialogBuilder.setTitle("Delete Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete this Contact?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            viewModel?.deleteData(contactList[position])
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            adapter.notifyDataSetChanged()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    fun filterContacts(query: String?) {
        val tempList = if (query.isNullOrBlank()) {
            contactList
        } else {
            contactList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                        it.phoneNo?.contains(query, ignoreCase = true) == true
            }
        }
        adapter.contactList = ArrayList(tempList)
        adapter.notifyDataSetChanged()
    }

    fun updateEmptyListVisibility() {
        if (binding.rv.adapter?.itemCount == 0) {
            binding.emptyImg.visibility = View.VISIBLE
            binding.rv.visibility = View.GONE
        } else {
            binding.emptyImg.visibility = View.GONE
            binding.rv.visibility = View.VISIBLE
        }
    }

    override fun isCallPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestCallPermission(position: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CALL_PHONE),
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
                    makePhoneCall()
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
        if (positionOfPermissionRequest != -1) {
            val contact = contactList[positionOfPermissionRequest]
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
}