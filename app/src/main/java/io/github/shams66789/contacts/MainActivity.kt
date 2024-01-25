package io.github.shams66789.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
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

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    var viewModel: MainActivityViewModel? = null
    var contactList = ArrayList<Contact>()
    lateinit var adapter : ContactAdapter
    var callingNo : Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateContact::class.java))
        }

        viewModel!!.data.observeForever{
            contactList.clear()
            it.map {
                contactList.add(it)
            }
            adapter.notifyDataSetChanged()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            1,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
                    viewModel!!.deleteData(contactList
                        .get(viewHolder.adapterPosition))
                } else {
                    callingNo = contactList
                        .get(viewHolder.adapterPosition)
                        .phoneNo?.toLong()

                    if (isCallPermissionGranted()) {
                        makePhoneCall()
                    } else {
                        requestCallPermission()
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }).attachToRecyclerView(binding.rv)

        binding.rv.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(contactList, this)
        binding.rv.adapter = adapter

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {
                    adapter.contactList = contactList
                    adapter.notifyDataSetChanged()
                } else {
                    if (newText.length == 0 || newText.isNullOrEmpty() ||
                        newText.isNullOrBlank()) {
                        adapter.contactList = contactList
                        adapter.notifyDataSetChanged()
                    } else {
                        var tempList = ArrayList<Contact>()
                        contactList.forEach{
                            if (it.name != null) {
                                if (it.name!!.contains(newText) ||
                                    it.phoneNo!!.contains(newText)) {
                                    tempList.add(it)
                                }
                            }
                        }
                        adapter.contactList = tempList
                        adapter.notifyDataSetChanged()
                    }
                }
                return true
            }

        })
    }

    fun isCallPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCallPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            }
        }
    }


    fun makePhoneCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$callingNo")
        startActivity(intent)
    }
}